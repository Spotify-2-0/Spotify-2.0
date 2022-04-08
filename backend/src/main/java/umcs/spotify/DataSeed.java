package umcs.spotify;

import com.mongodb.client.MongoClient;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import umcs.spotify.entity.Role;
import umcs.spotify.entity.RoleType;
import umcs.spotify.repository.RoleRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Component
public class DataSeed implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final MongoClient mongoClient;

    public DataSeed(RoleRepository roleRepository, MongoClient mongoClient) {
        this.roleRepository = roleRepository;
        this.mongoClient = mongoClient;
    }

    @Override
    public void run(String... args) {
        Arrays.stream(RoleType.values()).forEach(this::createRoleIfNotExists);
    }

    private void createRoleIfNotExists(RoleType roleType) {
        if (roleRepository.findByName(roleType).isEmpty()) {
            roleRepository.save(new Role(roleType));
            var database = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
            var bucket = GridFSBuckets.create(database, Constants.MONGO_BUCKET_NAME_TRACKS);
            try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("seed/track.mp3")) {
                bucket.uploadFromStream("", inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
