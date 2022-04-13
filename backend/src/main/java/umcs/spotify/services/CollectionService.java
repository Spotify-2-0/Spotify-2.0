package umcs.spotify.services;

import com.mongodb.Function;
import com.mongodb.client.MongoClient;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import umcs.spotify.Constants;
import umcs.spotify.contract.AddTrackRequest;
import umcs.spotify.contract.CollectionCreateRequest;
import umcs.spotify.dto.AudioTrackDto;
import umcs.spotify.dto.CollectionDto;
import umcs.spotify.entity.AudioTrack;
import umcs.spotify.entity.Collection;
import umcs.spotify.entity.Genre;
import umcs.spotify.entity.User;
import umcs.spotify.exception.RestException;
import umcs.spotify.helper.IOHelper;
import umcs.spotify.helper.ContextUserAccessor;
import umcs.spotify.helper.Mapper;
import umcs.spotify.repository.AudioTrackRepository;
import umcs.spotify.repository.CollectionRepository;
import umcs.spotify.repository.GenreRepository;
import umcs.spotify.repository.UserRepository;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
public class CollectionService {

    private final AudioTrackRepository audioTrackRepository;
    private final CollectionRepository collectionRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final MongoClient mongoClient;
    private final Mapper mapper;

    public CollectionService(
            CollectionRepository collectionRepository,
            AudioTrackRepository audioTrackRepository,
            GenreRepository genreRepository,
            UserRepository userRepository,
            UserService userService,
            MongoClient mongoClient,
            Mapper mapper
    ) {
        this.collectionRepository = collectionRepository;
        this.audioTrackRepository = audioTrackRepository;
        this.genreRepository = genreRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.mongoClient = mongoClient;
        this.mapper = mapper;
    }

    public List<CollectionDto> getCollections() {
        List<Collection> collections = collectionRepository.findAll();
        return collections.stream()
                .map(mapper::collectionToDto)
                .collect(Collectors.toList());
    }

    public CollectionDto getCollectionById(Long id) {
        Collection collection = collectionRepository.getById(id);

        return mapper.map(collection, CollectionDto.class);
    }

    public CollectionDto addCollection(CollectionCreateRequest request) {
        var currentUserEmail = ContextUserAccessor.getCurrentUserEmail();
        var user = userService.findUserByEmail(currentUserEmail);

        var db = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
        var imgBucket = GridFSBuckets.create(db, Constants.MONGO_BUCKET_NAME_AVATARS);
        try {
            var imgMongoRef = imgBucket.uploadFromStream("", request.getImage().getInputStream());
            var collectionToSave = new Collection();
            collectionToSave.setName(request.getName());
            collectionToSave.setType(request.getType());
            collectionToSave.setImageMongoRef(imgMongoRef.toString());
            collectionToSave.setTracks(List.of());
            collectionToSave.setUsers(List.of());
            collectionToSave.setOwner(user);

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

        var db = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
        var imgBucket = GridFSBuckets.create(db, Constants.MONGO_BUCKET_NAME_AVATARS);
        var mp3Bucket = GridFSBuckets.create(db, Constants.MONGO_BUCKET_NAME_TRACKS);
        imgBucket.delete(new ObjectId(track.getImageMongoRef()));
        mp3Bucket.delete(new ObjectId(track.getFileMongoRef()));

        collection.getTracks().remove(track);
        collectionRepository.save(collection);
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

        if (!IOHelper.isFileJpeg(request.getImage())) {
            throw new RestException(UNPROCESSABLE_ENTITY, "Invalid image file");
        }

        File tempFile = null;
        try {
            tempFile = IOHelper.multipartToTempFile(request.getTrack());

            var mp3Duration = IOHelper.getDurationOfMediaFile(tempFile)
                    .orElseThrow(() -> new RestException(INTERNAL_SERVER_ERROR, "Failed to read mp3"));

            var db = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
            var imgBucket = GridFSBuckets.create(db, Constants.MONGO_BUCKET_NAME_AVATARS);
            var mp3Bucket = GridFSBuckets.create(db, Constants.MONGO_BUCKET_NAME_TRACKS);

            var imgMongoRef = imgBucket.uploadFromStream("", request.getImage().getInputStream());
            var mp3MongoRef = mp3Bucket.uploadFromStream("", new FileInputStream(tempFile));

            var track = new AudioTrack();
            track.setPublishedDate(LocalDateTime.now());
            track.setDuration(mp3Duration);
            track.setGenres(genres);
            track.setArtists(artists);
            track.setName(request.getName());
            track.setFileMongoRef(mp3MongoRef.toString());
            track.setImageMongoRef(imgMongoRef.toString());

            audioTrackRepository.save(track);
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
