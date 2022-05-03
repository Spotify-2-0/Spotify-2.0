package umcs.spotify.services;

import com.mongodb.Function;
import com.mongodb.client.MongoClient;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import umcs.spotify.Constants;
import umcs.spotify.contract.AddTrackToCollectionRequest;
import umcs.spotify.contract.CollectionCreateRequest;
import umcs.spotify.contract.UpdateCollectionRequest;
import umcs.spotify.dto.AudioTrackDto;
import umcs.spotify.dto.CollectionDto;
import umcs.spotify.entity.AudioTrack;
import umcs.spotify.entity.Collection;
import umcs.spotify.entity.User;
import umcs.spotify.exception.RestException;
import umcs.spotify.helper.ContextUserAccessor;
import umcs.spotify.helper.Mapper;
import umcs.spotify.repository.AudioTrackRepository;
import umcs.spotify.repository.CollectionRepository;
import umcs.spotify.repository.UserRepository;

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
    private final UserService userService;
    private final MongoClient mongoClient;
    private final Mapper mapper;

    public CollectionService(
            CollectionRepository collectionRepository,
            AudioTrackRepository audioTrackRepository,
            UserRepository userRepository, UserService userService,
            MongoClient mongoClient,
            Mapper mapper
    ) {
        this.collectionRepository = collectionRepository;
        this.audioTrackRepository = audioTrackRepository;
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

        collection.setDuration(collection.getDuration().minus(track.getDuration()));

        collection.getTracks().remove(track);
        collectionRepository.save(collection);
    }

    public AudioTrackDto addTrack(long collectionId, AddTrackToCollectionRequest addTrackToCollectionRequest) {
        var collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RestException(NOT_FOUND, "Collection not found"));

        var currentUserEmail = ContextUserAccessor.getCurrentUserEmail();

        var user = userService.findUserByEmail(currentUserEmail);

        if (!collection.getOwner().getId().equals(user.getId())) {
            throw new RestException(FORBIDDEN, "You are not owner of this collection");
        }

        var audioTrack = audioTrackRepository.findById(addTrackToCollectionRequest.getId())
                .orElseThrow(() -> new RestException(NOT_FOUND, "Audio track not found"));

        List<AudioTrack> audioTracks = collection.getTracks();
        audioTracks.add(audioTrack);

        collection.setDuration(collection.getDuration().plus(audioTrack.getDuration()));

        collection.setTracks(audioTracks);

        collectionRepository.save(collection);

        return mapper.map(audioTrack, AudioTrackDto.class);
    }

    public void updateCollection(long id, UpdateCollectionRequest request) {
        var collection = collectionRepository.getById(id);

        var currentUserEmail = ContextUserAccessor.getCurrentUserEmail();
        var user = userService.findUserByEmail(currentUserEmail);

        if (!collection.getOwner().getId().equals(user.getId())) {
            throw new RestException(FORBIDDEN, "You are not owner of this collection");
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
        try {
            collectionRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new RestException(NOT_FOUND, ex.getLocalizedMessage());
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
        collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RestException(NOT_FOUND, "collection not found"));
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
