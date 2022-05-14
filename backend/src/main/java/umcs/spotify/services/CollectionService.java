package umcs.spotify.services;

import com.mongodb.Function;
import com.mongodb.client.MongoClient;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import umcs.spotify.Constants;
import umcs.spotify.contract.AddTrackRequest;
import umcs.spotify.contract.CollectionCreateRequest;
import umcs.spotify.contract.UpdateCollectionRequest;
import umcs.spotify.dto.AudioTrackDto;
import umcs.spotify.dto.CollectionDto;
import umcs.spotify.entity.*;
import umcs.spotify.entity.Collection;
import umcs.spotify.exception.RestException;
import umcs.spotify.helper.ContextUserAccessor;
import umcs.spotify.helper.IOHelper;
import umcs.spotify.helper.Mapper;
import umcs.spotify.repository.AudioTrackRepository;
import umcs.spotify.repository.CollectionRepository;
import umcs.spotify.repository.GenreRepository;
import umcs.spotify.repository.UserRepository;

import javax.transaction.Transactional;
import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
public class CollectionService {

    private final AudioTrackRepository audioTrackRepository;
    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final UserService userService;
    private final MongoClient mongoClient;
    private final Mapper mapper;

    public CollectionService(
            CollectionRepository collectionRepository,
            AudioTrackRepository audioTrackRepository,
            UserRepository userRepository, GenreRepository genreRepository, UserService userService,
            MongoClient mongoClient,
            Mapper mapper
    ) {
        this.collectionRepository = collectionRepository;
        this.audioTrackRepository = audioTrackRepository;
        this.userRepository = userRepository;
        this.genreRepository = genreRepository;
        this.userService = userService;
        this.mongoClient = mongoClient;
        this.mapper = mapper;
    }

    public List<CollectionDto> getCollections() {
        List<Collection> collections = collectionRepository.findAll();
        return collections.stream()
                .filter(collection -> collection.getType() != CollectionType.FAVORITES)
                .map(mapper::collectionToDto)
                .collect(Collectors.toList());
    }

    public CollectionDto getCollectionById(Long id) {
        var collection = collectionRepository.findById(id)
                .orElseThrow(() -> new RestException(NOT_FOUND, "Collection not found"));

        if(collection.getType() == CollectionType.FAVORITES) {
            throw new RestException(FORBIDDEN, "you cannot get favourites from here");
        }

        return mapper.map(collection, CollectionDto.class);
    }

    public List<CollectionType> getCollectionTypes() {
        return Arrays.stream(CollectionType.values())
                .filter(collectionType -> collectionType != CollectionType.FAVORITES)
                .collect(Collectors.toList());
    }

    public CollectionDto addCollection(CollectionCreateRequest request) {

        if(request.getType() == CollectionType.FAVORITES) {
            throw new RestException(FORBIDDEN, "You cannot create favourites");
        }

        var currentUserEmail = ContextUserAccessor.getCurrentUserEmail();
        var user = userService.findUserByEmail(currentUserEmail);

        var db = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
        var imgBucket = GridFSBuckets.create(db, Constants.MONGO_BUCKET_NAME_AVATARS);
        try {
            var img = request.getImage();
            var collectionToSave = new Collection();
            if(img != null) {
                var imgMongoRef = imgBucket.uploadFromStream("", img.getInputStream());
                collectionToSave.setImageMongoRef(imgMongoRef.toString());
            }else {
                collectionToSave.setImageMongoRef(null);
            }
            collectionToSave.setName(request.getName());
            collectionToSave.setType(request.getType());
            collectionToSave.setTracks(List.of());
            collectionToSave.setUsers(List.of());
            collectionToSave.setOwner(user);
            collectionToSave.setPublishedDate(LocalDateTime.now());
            collectionToSave.setViews(0L);
            collectionToSave.setDuration(Duration.ZERO);

            Collection savedCollection = collectionRepository.save(collectionToSave);
            return mapper.map(savedCollection, CollectionDto.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RestException(INTERNAL_SERVER_ERROR, "Error while saving files");
        }
    }

    public void removeTrack(long collectionId, long trackId) {
        var collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RestException(NOT_FOUND, "Collection not found"));

        var currentUserEmail = ContextUserAccessor.getCurrentUserEmail();

        var user = userService.findUserByEmail(currentUserEmail);

        if (!collection.getOwner().getId().equals(user.getId())) {
            throw new RestException(FORBIDDEN, "You are not owner of this collection");
        }

        var track = audioTrackRepository.findById(trackId)
                .orElseThrow(() -> new RestException(NOT_FOUND, "Track not found"));


        List<AudioTrack> tracks = collection.getTracks();

        tracks = tracks.stream()
                .filter(trackInList -> trackInList.getId() != trackId)
                .collect(Collectors.toList());

        collection.setTracks(tracks);
        collection.setDuration(collection.getDuration().minus(track.getDuration()));
        collectionRepository.save(collection);

        var db = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
        var mp3Bucket = GridFSBuckets.create(db, Constants.MONGO_BUCKET_NAME_TRACKS);
        mp3Bucket.delete(new ObjectId(track.getFileMongoRef()));

        audioTrackRepository.delete(track);
    }

    public AudioTrackDto addTrack(long collectionId, AddTrackRequest request) {
        var collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RestException(NOT_FOUND, "Collection not found"));

        var currentUserEmail = ContextUserAccessor.getCurrentUserEmail();

        var user = userService.findUserByEmail(currentUserEmail);

        if (!collection.getOwner().getId().equals(user.getId())) {
            throw new RestException(FORBIDDEN, "You are not owner of this collection");
        }

        var genresIds = request.getGenres() == null ? new ArrayList<Long>() : request.getGenres();
        var genres = genreRepository.findAllById(genresIds);

        findMissing(genres, genresIds, (genre, ids) -> genre.getId().equals(ids), Genre::getId)
                .ifPresent(missing -> { throw new RestException(NOT_FOUND, "Invalid genres ids: ({})", missing); });

        var artistsIds = request.getGenres() == null ? new ArrayList<Long>() : request.getArtists();
        var artists = userRepository.findAllById(artistsIds);

        findMissing(artists, artistsIds, (artist, ids) -> artist.getId().equals(ids), User::getId)
                .ifPresent(missing -> { throw new RestException(NOT_FOUND, "Invalid artists ids: ({})", missing); });


        File tempFile = null;
        try {
            tempFile = IOHelper.multipartToTempFile(request.getTrack());

            var mp3Duration = IOHelper.getDurationOfMediaFile(tempFile)
                    .orElseThrow(() -> new RestException(INTERNAL_SERVER_ERROR, "Failed to read mp3"));

            var db = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
            var mp3Bucket = GridFSBuckets.create(db, Constants.MONGO_BUCKET_NAME_TRACKS);

            var mp3MongoRef = mp3Bucket.uploadFromStream("", new FileInputStream(tempFile));

            var track = new AudioTrack();
            track.setPublishedDate(LocalDateTime.now());
            track.setDuration(mp3Duration);
            track.setGenres(genres);
            track.setArtists(artists);
            track.setName(request.getName());
            track.setFileMongoRef(mp3MongoRef.toString());

            audioTrackRepository.save(track);

            List<AudioTrack> tracks= collection.getTracks();
            tracks.add(track);
            collection.setTracks(tracks);
            collection.setDuration(collection.getDuration().plus(track.getDuration()));
            collectionRepository.save(collection);

            tempFile.delete();
            return mapper.map(track, AudioTrackDto.class);
        } catch (IOException e) {
            e.printStackTrace();
            if (tempFile != null) {
                tempFile.delete();
            }
            throw new RestException(INTERNAL_SERVER_ERROR, "Error while saving files");
        }
    }

    public void updateCollection(long id, UpdateCollectionRequest request) {
        var collection = collectionRepository.getById(id);

        var currentUserEmail = ContextUserAccessor.getCurrentUserEmail();
        var user = userService.findUserByEmail(currentUserEmail);

        if (!collection.getOwner().getId().equals(user.getId())) {
            throw new RestException(FORBIDDEN, "You are not owner of this collection");
        }

        if(request.getType() == CollectionType.FAVORITES) {
            throw new RestException(FORBIDDEN, "You cannot change collection type to favourites");
        }

        if (request.getName() != null)
            collection.setName(request.getName());
        if (request.getType() != null)
            collection.setType(request.getType());
        if (request.getImage() != null) {
            var db = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
            var imgBucket = GridFSBuckets.create(db, Constants.MONGO_BUCKET_NAME_AVATARS);
            try {
                imgBucket.delete(new ObjectId(collection.getImageMongoRef()));
                var imgMongoRef = imgBucket.uploadFromStream("", request.getImage().getInputStream());
                collection.setImageMongoRef(imgMongoRef.toString());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RestException(INTERNAL_SERVER_ERROR, "Error while saving files");
            }
        }
        collectionRepository.save(collection);
    }

    public void deleteCollection(Long id) {
        var collection = collectionRepository.findById(id)
                .orElseThrow(() -> new RestException(NOT_FOUND, "Collection not found"));

        var currentUserEmail = ContextUserAccessor.getCurrentUserEmail();
        var user = userService.findUserByEmail(currentUserEmail);

        if (!collection.getOwner().getId().equals(user.getId())) {
            throw new RestException(FORBIDDEN, "You are not owner of this collection");
        }

        if(collection.getType() == CollectionType.FAVORITES) {
            throw new RestException(FORBIDDEN, "You cannot delete favourites");
        }

        try {
            collectionRepository.deleteById(id);
        } catch (Exception ex) {
            throw new RestException(INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());
        }
    }


    public InputStreamResource getCollectionAvatar(long id) {
        var collection = collectionRepository.findById(id)
                .orElseThrow(() -> new RestException(NOT_FOUND, "collection not found"));

        var database = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
        var bucket = GridFSBuckets.create(database, Constants.MONGO_BUCKET_NAME_AVATARS);
        var stream = bucket.openDownloadStream(new ObjectId(collection.getImageMongoRef()));

        return new InputStreamResource(stream);
    }
    public void followCollection(long collectionId) {
        var collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RestException(NOT_FOUND, "collection not found"));

        if(collection.getType() == CollectionType.FAVORITES) {
            throw new RestException(FORBIDDEN, "You cannot follow favourites");
        }

        var email = ContextUserAccessor.getCurrentUserEmail();
        var user = userService.findUserByEmail(email);

        List<Collection> collections = user.getCollections();

        if(collections.stream().anyMatch(collectionInList -> collectionInList.getId() == collectionId)) {
            throw new RestException(UNPROCESSABLE_ENTITY, "you're already following this collection");
        }

        collections.add(collection);
        userRepository.save(user);
    }

    public void unfollowCollection(long collectionId) {
        var collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RestException(NOT_FOUND, "collection not found"));

        if(collection.getType() == CollectionType.FAVORITES) {
            throw new RestException(FORBIDDEN, "You cannot unfollow favourites");
        }

        var email = ContextUserAccessor.getCurrentUserEmail();
        var user = userService.findUserByEmail(email);

        List<Collection> collections = user.getCollections();

        if (collections.stream().noneMatch(collectionInList -> collectionInList.getId() == collectionId)) {
            throw new RestException(UNPROCESSABLE_ENTITY, "you're not following this collection");
        }

        collections = collections.stream()
                .filter(collectionInList -> collectionInList.getId() != collectionId)
                .collect(Collectors.toList());

        user.setCollections(collections);
        userRepository.save(user);
    }

    public void addVisitToCollection(long collectionId) {
        var collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RestException(NOT_FOUND, "collection not found"));

        collection.setViews(collection.getViews()+1);

        collectionRepository.save(collection);
    }

    public List<CollectionDto> getUserCollections() {
        var currentUserEmail = ContextUserAccessor.getCurrentUserEmail();
        var user = userService.findUserByEmail(currentUserEmail);

        return user.getCollections().stream()
                .filter(collection -> collection.getType() != CollectionType.FAVORITES)
                .map(mapper::collectionToDto)
                .collect(Collectors.toList());
    }

    private <A, B> Optional<String> findMissing(
        List<A> src,
        List<B> dst,
        BiFunction<A, B, Boolean> cmp,
        Function<A, ?> sup
    ) {
        if (src.size() == dst.size()) {
            return Optional.empty();
        }

        return Optional.of(src.stream()
            .filter(e -> dst.stream().noneMatch(s -> cmp.apply(e, s)))
            .map(e -> sup.apply(e).toString())
            .collect(Collectors.joining(", ")));
    }
}
