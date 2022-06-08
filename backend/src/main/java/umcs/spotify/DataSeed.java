package umcs.spotify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import umcs.spotify.entity.*;
import umcs.spotify.helper.IOHelper;
import umcs.spotify.repository.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Component
public class DataSeed implements CommandLineRunner {

    private final GenreRepository genreRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CollectionRepository collectionRepository;
    private final AudioTrackRepository audioTrackRepository;
    private final MongoClient mongoClient;

    public DataSeed(
            GenreRepository genreRepository,
            AudioTrackRepository audioTrackRepository,
            CollectionRepository collectionRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            MongoClient mongoClient) {
        this.genreRepository = genreRepository;
        this.audioTrackRepository = audioTrackRepository;
        this.collectionRepository = collectionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.mongoClient = mongoClient;
    }

    @Override
    public void run(String... args) {
        if (roleRepository.findByName(RoleType.ADMINISTRATOR).isEmpty()) {
            Arrays.stream(RoleType.values()).forEach(this::createRoleIfNotExists);
            try {
                seedData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createRoleIfNotExists(RoleType roleType) {
        if (roleRepository.findByName(roleType).isEmpty()) {
            roleRepository.save(new Role(roleType));
        }
    }

    private GridFSBucket getBucket(String bucket) {
        var database = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
        return GridFSBuckets.create(database, bucket);
    }

    @Transactional
    public void seedData() throws IOException {
        var seedTempDir = new File(IOHelper.TEMP_DIR, "seed");
        seedTempDir.mkdirs();

        var genres = IOHelper.readAllLinesResource("seed/genres.txt");
        genres.forEach(genreName -> {
            var genre = new Genre();
            genre.setName(genreName);
            genreRepository.save(genre);
        });

        var json = new String(IOHelper.readAllBytesResource("seed/collections.json"));
        var objectMapper = new ObjectMapper();
        var seedData = objectMapper.readValue(json, SeedData.class);

        var bucketImages = getBucket(Constants.MONGO_BUCKET_NAME_AVATARS);
        var bucketTracks = getBucket(Constants.MONGO_BUCKET_NAME_TRACKS);
        for (var seedUser : seedData.users) {
            var imgRef = bucketImages.uploadFromStream("", IOHelper.getResource("seed/" + seedUser.avatar));
            var user = new User();
            user.setFirstName(seedUser.firstName);
            user.setLastName(seedUser.lastName);
            user.setDisplayName(seedUser.displayName);
            user.setEmail(seedUser.email);
            user.setAvatarMongoRef(imgRef.toString());
            userRepository.save(user);
        }

        for (var seedCollection : seedData.collections) {
            var imgRef = bucketImages.uploadFromStream("", IOHelper.getResource("seed/" + seedCollection.image));
            var collection = new Collection();
            collection.setName(seedCollection.name);
            collection.setType(seedCollection.type);
            collection.setOwner(userRepository.getById(seedCollection.ownerId));
            collection.setImageMongoRef(imgRef.toString());

            var tracks = seedCollection.tracks.stream()
                    .map(rethrowFunction(seedTrack -> {
                        var temp = new File(seedTempDir, seedTrack.mp3);
                        temp.getParentFile().mkdirs();
                        Files.copy(IOHelper.getResource("seed/" + seedTrack.mp3), temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        var mp3Ref = bucketTracks.uploadFromStream("", new FileInputStream(temp));
                        var track = new AudioTrack();
                        track.setName(seedTrack.name);
                        track.setFileMongoRef(mp3Ref.toString());
                        track.setDuration(IOHelper.getDurationOfMediaFile(temp).get());
                        track.setArtists(userRepository.findAllById(seedTrack.artistsIds));
                        track.setPublishedDate(LocalDateTime.now());
                        temp.delete();
                        return track;
                    })).toList();

            audioTrackRepository.saveAll(tracks);

            collection.setTracks(tracks);
            collectionRepository.save(collection);
        }

        IOHelper.deleteDir(seedTempDir);
    }

    @FunctionalInterface
    public interface Function_WithExceptions<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    @SuppressWarnings ("unchecked")
    private static <E extends Throwable> void throwAsUnchecked(Exception exception) throws E { throw (E)exception; }

    public static <T, R, E extends Exception> Function<T, R> rethrowFunction(Function_WithExceptions<T, R, E> function) throws E {
        return t -> {
            try { return function.apply(t); }
            catch (Exception exception) { throwAsUnchecked(exception); return null; }
        };
    }

    public static class SeedData {
        public List<SeedUser> users;
        public List<SeedCollection> collections;
    }

    public static class SeedUser {
        public int id;
        public String firstName;
        public String lastName;
        public String displayName;
        public String email;
        public String avatar;
    }

    public static class SeedCollection {
        public String name;
        public String image;
        public CollectionType type;
        public Long ownerId;
        public List<SeedTrack> tracks;
    }

    public static class SeedTrack {
        public String name;
        public String mp3;
        public List<String> tags;
        public List<Long> artistsIds;
    }
}
