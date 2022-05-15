package umcs.spotify.services;

import org.springframework.stereotype.Service;
import umcs.spotify.dto.CollectionDto;
import umcs.spotify.entity.AudioTrack;
import umcs.spotify.entity.Collection;
import umcs.spotify.entity.CollectionType;
import umcs.spotify.entity.User;
import umcs.spotify.exception.RestException;
import umcs.spotify.helper.ContextUserAccessor;
import umcs.spotify.helper.Mapper;
import umcs.spotify.repository.AudioTrackRepository;
import umcs.spotify.repository.CollectionRepository;
import umcs.spotify.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static umcs.spotify.Constants.FAVOURITES_COLLECTION_PREFIX;

@Service
public class FavouritesService {
    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final AudioTrackRepository audioTrackRepository;
    private final Mapper mapper;

    public FavouritesService(CollectionRepository collectionRepository, UserRepository userRepository, AudioTrackRepository audioTrackRepository, Mapper mapper) {
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
        this.audioTrackRepository = audioTrackRepository;
        this.mapper = mapper;
    }

    public void createFavourites() {
        var currentUserEmail = ContextUserAccessor.getCurrentUserEmail();
        var user = findUserByEmail(currentUserEmail);

        var collectionToSave = new Collection();
        collectionToSave.setName(FAVOURITES_COLLECTION_PREFIX + user.getId());
        collectionToSave.setType(CollectionType.FAVORITES);
        collectionToSave.setTracks(List.of());
        collectionToSave.setUsers(List.of());
        collectionToSave.setOwner(user);
        collectionToSave.setPublishedDate(LocalDateTime.now());
        collectionToSave.setViews(0L);
        collectionToSave.setDuration(Duration.ZERO);
        collectionToSave.setImageMongoRef(null);

        collectionRepository.save(collectionToSave);
    }

    public CollectionDto getFavourites() {
        var currentUserEmail = ContextUserAccessor.getCurrentUserEmail();
        var user = findUserByEmail(currentUserEmail);

        var collection = collectionRepository.findByNameAndType(FAVOURITES_COLLECTION_PREFIX + user.getId(), CollectionType.FAVORITES)
                .orElseThrow(() -> new RestException(NOT_FOUND, "favourites not found"));

        return mapper.map(collection, CollectionDto.class);
    }

    public CollectionDto addTrackToFavourites(Long trackId) {
        var currentUserEmail = ContextUserAccessor.getCurrentUserEmail();
        var user = findUserByEmail(currentUserEmail);

        var collection = collectionRepository.findByNameAndType(FAVOURITES_COLLECTION_PREFIX + user.getId(), CollectionType.FAVORITES)
                .orElseThrow(() -> new RestException(NOT_FOUND, "favourites not found"));

        var track = audioTrackRepository.findById(trackId)
                .orElseThrow(() -> new RestException(NOT_FOUND, "track not found"));

        List<AudioTrack> tracks = collection.getTracks();
        if(tracks.stream().anyMatch(audioTrack -> Objects.equals(audioTrack.getId(), trackId))) {
            throw new RestException(UNPROCESSABLE_ENTITY, "you already have this song in favourites");
        }

        tracks.add(track);
        collection.setTracks(tracks);
        var savedCollection = collectionRepository.save(collection);
        return mapper.map(savedCollection, CollectionDto.class);
    }

    public void removeTrackFromFavourites(Long trackId) {
        var currentUserEmail = ContextUserAccessor.getCurrentUserEmail();
        var user = findUserByEmail(currentUserEmail);

        var collection = collectionRepository.findByNameAndType(FAVOURITES_COLLECTION_PREFIX + user.getId(), CollectionType.FAVORITES)
                .orElseThrow(() -> new RestException(NOT_FOUND, "favourites not found"));

        audioTrackRepository.findById(trackId)
                .orElseThrow(() -> new RestException(NOT_FOUND, "track not found"));

        List<AudioTrack> tracks = collection.getTracks();
        if(tracks.stream().noneMatch(audioTrack -> Objects.equals(audioTrack.getId(), trackId))) {
            throw new RestException(UNPROCESSABLE_ENTITY, "you don't have this song in your favorites");
        }

        tracks = tracks.stream()
                .filter(audioTrack -> !Objects.equals(audioTrack.getId(), trackId))
                .collect(Collectors.toList());
        collection.setTracks(tracks);
        collectionRepository.save(collection);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RestException(NOT_FOUND, "user with email {} not found", email));
    }
}
