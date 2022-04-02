package umcs.spotify.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBuckets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import umcs.spotify.Constants;
import umcs.spotify.helper.Formatter;

import java.util.ArrayList;

@ConfigurationProperties("mongo")
@Getter
@Setter
public class MongoConfig {

    @Value( "${mongo.user}" )
    private String mongoUser;
    @Value( "${mongo.pass}" )
    private String mongoPassword;
    @Value( "${mongo.host}" )
    private String mongoHost;
    @Value( "${mongo.port}" )
    private Integer mongoPort;

    @Bean
    @Primary
    public MongoClient getMongoClient() {
        var url = Formatter.format(
            "mongodb://{}:{}@{}:{}",
            mongoUser,
            mongoPassword,
            mongoHost,
            mongoPort
        );
        return MongoClients.create(url);
    }

}
