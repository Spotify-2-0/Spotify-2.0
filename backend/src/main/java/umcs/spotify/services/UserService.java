package umcs.spotify.services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import umcs.spotify.Constants;
import umcs.spotify.dto.UserDto;
import umcs.spotify.entity.User;
import umcs.spotify.exception.RestException;
import umcs.spotify.helper.AvatarHelper;
import umcs.spotify.helper.ContextUserAccessor;
import umcs.spotify.helper.Formatter;
import umcs.spotify.helper.PinCodeHelper;
import umcs.spotify.repository.UserRepository;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.springframework.http.HttpStatus.*;
import static umcs.spotify.helper.AvatarHelper.toStream;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final EmailService emailService;
    private final MongoClient mongoClient;
    private final ModelMapper mapper;

    public UserService(
            UserRepository userRepository,
            EmailService emailService,
            MongoClient mongoClient,
            ModelMapper mapper
    ) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.mongoClient = mongoClient;
        this.mapper = mapper;
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RestException(NOT_FOUND, "user with email {} not found", email));
    }

    public UserDto getCurrentUser() {
        var email = ContextUserAccessor.getCurrentUserEmail();
        return mapper.map(findUserByEmail(email), UserDto.class);
    }

    public void sendEmailConfirmationCodeForCurrentUser() {
        var email = ContextUserAccessor.getCurrentUserEmail();
        var currentUser = findUserByEmail(email);
        sendEmailConfirmationCode(currentUser);
    }

    public void sendEmailConfirmationCode(User user) {
        user.setEmailConfirmationCode(PinCodeHelper.generateRandomPin(5));
        userRepository.save(user);
        emailService.sendEmailVerificationEmail(user);
    }

    public boolean confirmEmail(String code) {
        var email = ContextUserAccessor.getCurrentUserEmail();
        var currentUser = findUserByEmail(email);
        if (currentUser.getEmailConfirmationCode().equals(code)) {
            currentUser.setEmailConfirmed(true);
            userRepository.save(currentUser);
            return true;
        }
        return false;
    }

    @Async
    public void assignDefaultAvatarForCurrentUser() {
        var email = ContextUserAccessor.getCurrentUserEmail();
        var currentUser = findUserByEmail(email);
        assignDefaultAvatar(currentUser);
    }

    @Async
    public void assignDefaultAvatar(User user) {
        var database = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
        var bucket = GridFSBuckets.create(database, Constants.MONGO_BUCKET_NAME_AVATARS);

        var initials = Formatter.format(
            "{}{}",
            user.getFirstName().charAt(0),
            user.getLastName().charAt(0)
        );
        var generatedAvatar = AvatarHelper.generateAvatarFromInitials(initials, 300, 300);


        try {
            var stream = AvatarHelper.toStream(generatedAvatar);
            var mongoRef = bucket.uploadFromStream("", stream).toString();
            user.setAvatarMongoRef(mongoRef);
            userRepository.save(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ResponseEntity<InputStreamResource> getUserAvatar(long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new RestException(NOT_FOUND, "User not found"));

        var database = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
        var bucket = GridFSBuckets.create(database, Constants.MONGO_BUCKET_NAME_AVATARS);
        var stream = bucket.openDownloadStream(new ObjectId(user.getAvatarMongoRef()));

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(stream));
    }

    public void uploadAvatar(MultipartFile multipartFile) {
        var email = ContextUserAccessor.getCurrentUserEmail();
        var currentUser = findUserByEmail(email);

        var database = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
        var bucket = GridFSBuckets.create(database, Constants.MONGO_BUCKET_NAME_AVATARS);

        if (!AvatarHelper.isValidJpeg(multipartFile)) {
            throw new RestException(UNPROCESSABLE_ENTITY, "File is not jpeg");
        }

        try {
            var id = bucket.uploadFromStream("", multipartFile.getInputStream());
            currentUser.setAvatarMongoRef(id.toString());
            userRepository.save(currentUser);
        } catch (IOException e) {
            throw new RestException(INTERNAL_SERVER_ERROR, "Failed to upload image");
        }
    }

}
