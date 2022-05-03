package umcs.spotify.services;

import com.mongodb.Function;
import com.mongodb.client.MongoClient;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import umcs.spotify.Constants;
import umcs.spotify.contract.AddTrackRequest;
import umcs.spotify.dto.AudioTrackDto;
import umcs.spotify.dto.GenreDto;
import umcs.spotify.dto.UserDto;
import umcs.spotify.entity.AudioTrack;
import umcs.spotify.entity.Genre;
import umcs.spotify.entity.User;
import umcs.spotify.exception.RestException;
import umcs.spotify.helper.IOHelper;
import umcs.spotify.helper.Mapper;
import umcs.spotify.repository.AudioTrackRepository;
import umcs.spotify.repository.CollectionRepository;
import umcs.spotify.repository.GenreRepository;
import umcs.spotify.repository.UserRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
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
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    private final Mapper mapper;

    public TrackService(
            JwtService jwtService,
            MongoClient mongoClient,
            UserDetailsService userDetailsService,
            AudioTrackRepository audioTrackRepository, CollectionRepository collectionRepository, GenreRepository genreRepository, UserRepository userRepository, UserService userService, Mapper mapper) {
        this.jwtService = jwtService;
        this.mongoClient = mongoClient;
        this.userDetailsService = userDetailsService;
        this.audioTrackRepository = audioTrackRepository;
        this.collectionRepository = collectionRepository;
        this.genreRepository = genreRepository;
        this.userRepository = userRepository;
        this.userService = userService;
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
        id = bucket.find().first().getObjectId().toString();

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

    public AudioTrackDto addTrack(AddTrackRequest request) {

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
            var imgBucket = GridFSBuckets.create(db, Constants.MONGO_BUCKET_NAME_AVATARS);

            var mp3MongoRef = mp3Bucket.uploadFromStream("", new FileInputStream(tempFile));
            var imgMongoRef = imgBucket.uploadFromStream("", request.getTrackAvatar().getInputStream());

            var track = new AudioTrack();
            track.setPublishedDate(LocalDateTime.now());
            track.setDuration(mp3Duration);
            track.setGenres(genres);
            track.setArtists(artists);
            track.setName(request.getName());
            track.setFileMongoRef(mp3MongoRef.toString());
            track.setAvatarMongoRef(imgMongoRef.toString());

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

    public void removeTrack(Long id) {

        var track = audioTrackRepository.findById(id)
                .orElseThrow(() -> new RestException(NOT_FOUND, "Track not found"));

        var db = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
        var mp3Bucket = GridFSBuckets.create(db, Constants.MONGO_BUCKET_NAME_TRACKS);
        mp3Bucket.delete(new ObjectId(track.getFileMongoRef()));


        audioTrackRepository.delete(track);
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

    public InputStreamResource getTrackAvatar(long id) {
        var track = audioTrackRepository.findById(id)
                .orElseThrow(() -> new RestException(NOT_FOUND, "track not found"));

        var database = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
        var bucket = GridFSBuckets.create(database, Constants.MONGO_BUCKET_NAME_AVATARS);
        var stream = bucket.openDownloadStream(new ObjectId(track.getAvatarMongoRef()));

        return new InputStreamResource(stream);
    }
}
