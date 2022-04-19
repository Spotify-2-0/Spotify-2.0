package umcs.spotify.services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import umcs.spotify.Constants;
import umcs.spotify.exception.RestException;
import umcs.spotify.helper.IOHelper;

import java.io.IOException;

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

    public TrackService(
        JwtService jwtService,
        MongoClient mongoClient,
        UserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.mongoClient = mongoClient;
        this.userDetailsService = userDetailsService;
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
}
