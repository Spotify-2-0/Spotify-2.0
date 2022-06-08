package umcs.spotify.services;

import com.mongodb.MongoGridFSException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import umcs.spotify.Constants;
import umcs.spotify.exception.RestException;

@Service
public class AvatarService {

    private final MongoClient mongoClient;

    public AvatarService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public InputStreamResource getAvatar(String mongoRef) {
        try {
            var database = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
            var bucket = GridFSBuckets.create(database, Constants.MONGO_BUCKET_NAME_AVATARS);
            var stream = bucket.openDownloadStream(new ObjectId(mongoRef));

            return new InputStreamResource(stream);
        } catch (MongoGridFSException ex) {
            throw new RestException(HttpStatus.NOT_FOUND, ex.getLocalizedMessage());
        } catch (Exception ex) {
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());
        }

    }
}
