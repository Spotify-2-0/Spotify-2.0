package umcs.spotify.services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import umcs.spotify.Constants;
import umcs.spotify.dto.AudioTrackDto;
import umcs.spotify.dto.UserDto;
import umcs.spotify.entity.User;
import umcs.spotify.exception.RestException;
import umcs.spotify.helper.IOHelper;
import umcs.spotify.helper.Mapper;
import umcs.spotify.repository.AudioTrackRepository;
import umcs.spotify.repository.CollectionRepository;
import umcs.spotify.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpHeaders.CONTENT_RANGE;
import static org.springframework.http.HttpStatus.*;
import static umcs.spotify.helper.Formatter.format;

@Service
public class TrackService {

    private static final int MAX_CHUNK = 1024 * 1024;

    private final JwtService jwtService;
    private final MongoClient mongoClient;
    private final UserDetailsService userDetailsService;
    private final AudioTrackRepository audioTrackRepository;
    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    public TrackService(
            JwtService jwtService,
            MongoClient mongoClient,
            UserDetailsService userDetailsService,
            AudioTrackRepository audioTrackRepository, CollectionRepository collectionRepository, UserRepository userRepository, Mapper mapper) {
        this.jwtService = jwtService;
        this.mongoClient = mongoClient;
        this.userDetailsService = userDetailsService;
        this.audioTrackRepository = audioTrackRepository;
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public ResponseEntity<?> getTrackChunked(String id, String token, String range) {
        var userName = jwtService.extractUsername(token);
        var userDetails = userDetailsService.loadUserByUsername(userName);
        if (!jwtService.isTokenValid(token, userDetails)) {
            throw new RestException(FORBIDDEN, "You do not have access to this resource");
        }

        var database = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
        var bucket = GridFSBuckets.create(database, Constants.MONGO_BUCKET_NAME_TRACKS);

        // TODO: Ignore ID for now as there is no upload mechanism, get first track seeded
//        id = bucket.find().first().getObjectId().toString();

        try (var stream = bucket.openDownloadStream(new ObjectId(id))) {
            var ranges = range.split("-");
            var fileSize = stream.getGridFSFile().getLength();
            var start = Long.parseLong(ranges[0].substring(6));
            long end;
            if (ranges.length > 1) {
                end = Long.parseLong(ranges[1]);
            } else {
                end = Math.min(start + MAX_CHUNK, fileSize - 1);
            }

            var bytesToCopy = end - start + 1;
            try (var resource = IOHelper.copyInputStreamRange(stream, start, end)) {
                return ResponseEntity.status(PARTIAL_CONTENT)
                        .header(CONTENT_TYPE, "audio/mpeg")
                        .header(ACCEPT_RANGES, "bytes")
                        .header(CONTENT_LENGTH, String.valueOf(bytesToCopy))
                        .header(CONTENT_RANGE, format("bytes {}-{}/{}", start, end, fileSize))
                        .body(new InputStreamResource(resource));
            } catch (IOException e) {
               throw new RestException(INTERNAL_SERVER_ERROR, "Failed to get chunked data");
            }
        }
    }

    public AudioTrackDto getAudioTrackDetails(Long id) {
        var track = audioTrackRepository.findById(id)
                .orElseThrow(() -> new RestException(NOT_FOUND, "Track not found"));

        return mapper.map(track, AudioTrackDto.class);
    }

    public List<AudioTrackDto> getTracks(Long collectionId) {
        if(Objects.isNull(collectionId)) {
            return audioTrackRepository.findAll().stream()
                    .map(audioTrack -> mapper.map(audioTrack, AudioTrackDto.class))
                    .collect(Collectors.toList());
        }
        var collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RestException(NOT_FOUND, "Collection not found"));

        return collection.getTracks().stream()
                .map(audioTrack -> mapper.map(audioTrack, AudioTrackDto.class))
                .collect(Collectors.toList());
    }

    public UserDto addArtistToTrack(Long trackId, Long userId) {
        var track = audioTrackRepository.findById(trackId)
                .orElseThrow(() -> new RestException(NOT_FOUND, "Track not found"));

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RestException(NOT_FOUND, "user not found"));

        List<User> artists = track.getArtists();

        if(artists.stream().anyMatch(userInList -> Objects.equals(userInList.getId(), userId))) {
            throw new RestException(UNPROCESSABLE_ENTITY, "This user is already an artist");
        }

        artists.add(user);
        track.setArtists(artists);
        audioTrackRepository.save(track);
        return mapper.userToDto(user);
    }

    public void removeArtistFromTrack(Long trackId, Long userId) {
        var track = audioTrackRepository.findById(trackId)
                .orElseThrow(() -> new RestException(NOT_FOUND, "Track not found"));

        userRepository.findById(userId)
                .orElseThrow(() -> new RestException(NOT_FOUND, "user not found"));

        List<User> artists = track.getArtists();

        if(artists.stream().noneMatch(userInList -> Objects.equals(userInList.getId(), userId))) {
            throw new RestException(UNPROCESSABLE_ENTITY, "This user is not the artist of this track");
        }

        artists = artists.stream()
                .filter(userInList -> !Objects.equals(userInList.getId(), userId))
                .collect(Collectors.toList());
        track.setArtists(artists);
        audioTrackRepository.save(track);
    }
}
