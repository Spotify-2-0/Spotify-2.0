package umcs.spotify.services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.gridfs.GridFSBuckets;
import net.jodah.expiringmap.ExpiringMap;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;
import umcs.spotify.Constants;
import umcs.spotify.dto.AddTrackDataToSelectDto;
import umcs.spotify.contract.PasswordChangeRequest;
import umcs.spotify.contract.PasswordResetPinToKeyRequest;
import umcs.spotify.contract.PasswordResetRequest;
import umcs.spotify.dto.UserDto;
import umcs.spotify.dto.UserPreferencesDto;
import umcs.spotify.entity.User;
import umcs.spotify.exception.RestException;
import umcs.spotify.helper.*;
import umcs.spotify.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
public class UserService {

    private static final Map<String, String> PASSWORD_RESET_PIN_CODE_CACHE = ExpiringMap
            .builder()
            .expiration(2, TimeUnit.MINUTES)
            .build();

    private static final Map<String, String> PASSWORD_RESET_KEY_CODE_CACHE = ExpiringMap
            .builder()
            .expiration(5, TimeUnit.MINUTES)
            .build();


    private final UserActivityService activityService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final MongoClient mongoClient;
    private final Mapper mapper;
    private final FavouritesService favouritesService;

    public UserService(
            UserActivityService activityService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            EmailService emailService,
            MongoClient mongoClient,
            Mapper mapper, FavouritesService favouritesService) {
        this.activityService = activityService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.mongoClient = mongoClient;
        this.mapper = mapper;
        this.favouritesService = favouritesService;
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
        return mapper.userToDto(findUserByEmail(email));
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

    public void sendEmailPasswordReset(String email) {
        userRepository.findByEmail(email).ifPresent((user) -> {
            var pin = PinCodeHelper.generateRandomPin(5);
            PASSWORD_RESET_PIN_CODE_CACHE.put(user.getEmail(), pin);
            userRepository.save(user);
            emailService.sendPasswordResetEmail(user, pin);
            activityService.addActivity(user,
                "Password reset requested",
                ContextUserAccessor.getRemoteAddres()
            );
        });
    }

    public boolean confirmEmail(String code) {
        var email = ContextUserAccessor.getCurrentUserEmail();
        var currentUser = findUserByEmail(email);
        if (currentUser.getEmailConfirmationCode().equals(code)) {
            currentUser.setEmailConfirmed(true);
            userRepository.save(currentUser);
            favouritesService.createFavourites();
            return true;
        }
        return false;
    }

    @Transactional
    public void changePreferences(String firstName, String lastName, String displayName) {
        var currentUser = findUserByEmail(ContextUserAccessor.getCurrentUserEmail());
        if(firstName != null){
            currentUser.setFirstName(firstName);
        }
        if(lastName != null){
            currentUser.setLastName(lastName);
        }
        if(displayName != null){
            currentUser.setDisplayName(displayName);
        }
    }

    public UserPreferencesDto getPreferences() {
        var email = ContextUserAccessor.getCurrentUserEmail();
        var currentUser = findUserByEmail(email);
        return mapper.map(currentUser, UserPreferencesDto.class);
    }

    public void assignDefaultAvatarForCurrentUser() {
        var email = ContextUserAccessor.getCurrentUserEmail();
        var currentUser = findUserByEmail(email);

        var database = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
        var bucket = GridFSBuckets.create(database, Constants.MONGO_BUCKET_NAME_AVATARS);
        var avatarMongoRef = currentUser.getAvatarMongoRef();

        if(avatarMongoRef != null){
            bucket.delete(new ObjectId(avatarMongoRef));
        }

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

    public void assignDefaultAvatarByUserId(long id){
        var user = userRepository.findById(id)
                .orElseThrow(() -> new RestException(NOT_FOUND, "User not found"));

        assignDefaultAvatar(user);
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

        if (!IOHelper.isFileJpeg(multipartFile)) {
            throw new RestException(UNPROCESSABLE_ENTITY, "File is not jpeg");
        }

        try {
            var id = bucket.uploadFromStream("", multipartFile.getInputStream());
            var avatarMongoRef = currentUser.getAvatarMongoRef();

            if(avatarMongoRef != null){
                bucket.delete(new ObjectId(avatarMongoRef));
            }

            currentUser.setAvatarMongoRef(id.toString());
            userRepository.save(currentUser);
        } catch (IOException e) {
            throw new RestException(INTERNAL_SERVER_ERROR, "Failed to upload image");
        }
    }

    public String getPasswordChangeKeyFromPinCode(PasswordResetPinToKeyRequest request) {
        var pin = PASSWORD_RESET_PIN_CODE_CACHE.get(request.getEmail());
        if (pin == null) {
            throw new RestException(UNAUTHORIZED, "Pin has expired");
        }
        if (!pin.equals(request.getPin())) {
            throw new RestException(UNAUTHORIZED, "Pin is invalid");
        }
        PASSWORD_RESET_PIN_CODE_CACHE.remove(request.getEmail());
        var user = findUserByEmail(request.getEmail());
        var resetKey = UUID.randomUUID().toString().replace("-", "");
        PASSWORD_RESET_KEY_CODE_CACHE.put(resetKey, user.getEmail());
        return resetKey;
    }

    @Transactional
    public void changePassword(PasswordChangeRequest request, Errors errors){
        if(errors.hasFieldErrors()){
            throw new RestException(BAD_REQUEST, FormValidatorHelper.returnFormattedErrors(errors));
        }

        var userDto = getCurrentUser();
        var user = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new RestException(NOT_FOUND, "Invalid or expired password reset key"));

        boolean isOldPasswordMatch = passwordEncoder.matches(request.getOldPassword(), user.getPassword());
        boolean isNewPasswordMatch = request.getNewPassword().equals(request.getRepeatedNewPassword());


        if(!isOldPasswordMatch){
            throw new RestException(UNAUTHORIZED, "Invalid password");
        }

        if(!isNewPasswordMatch){
            throw new RestException(BAD_REQUEST, "New and old password aren't matching each other");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }

    public void resetPassword(PasswordResetRequest request, Errors errors) {
        if (errors.hasFieldErrors()) {
            throw new RestException(BAD_REQUEST, FormValidatorHelper.returnFormattedErrors(errors));
        }

        var email = PASSWORD_RESET_KEY_CODE_CACHE.get(request.getKey());
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RestException(NOT_FOUND, "Invalid or expired password reset key"));

        PASSWORD_RESET_KEY_CODE_CACHE.remove(request.getKey());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        activityService.addActivity(user,
            "Password changed",
            ContextUserAccessor.getRemoteAddres()
        );
    }

    public List<AddTrackDataToSelectDto> findAllStartingWith(final String str) {
        final String loggedUserEmail = ContextUserAccessor.getCurrentUserEmail();

        return this.userRepository.findAllByDisplayNameStartingWithIgnoreCase(str).stream()
                .filter(user -> !user.getEmail().equals(loggedUserEmail) && user.isEmailConfirmed())
                .map(user -> new AddTrackDataToSelectDto(user.getId(), user.getDisplayName()))
                .collect(Collectors.toList());
    }
}
