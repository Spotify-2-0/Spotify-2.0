package umcs.spotify.services;

import net.jodah.expiringmap.ExpiringMap;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import umcs.spotify.contract.PasswordResetPinToKeyRequest;
import umcs.spotify.contract.PasswordResetRequest;
import umcs.spotify.dto.UserDto;
import umcs.spotify.entity.User;
import umcs.spotify.exception.RestException;
import umcs.spotify.helper.ContextUserAccessor;
import umcs.spotify.helper.FormValidatorHelper;
import umcs.spotify.helper.PinCodeHelper;
import umcs.spotify.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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


    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ModelMapper mapper;

    public UserService(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            EmailService emailService,
            ModelMapper mapper
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.emailService = emailService;
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

    public void sendEmailPasswordReset(String email) {
        var maybeUser = userRepository.findByEmail(email);
        maybeUser.ifPresent((user) -> {
            var pin = PinCodeHelper.generateRandomPin(5);
            PASSWORD_RESET_PIN_CODE_CACHE.put(user.getEmail(), pin);
            userRepository.save(user);
            emailService.sendPasswordResetEmail(user, pin);
        });
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
        PASSWORD_RESET_KEY_CODE_CACHE.put(user.getEmail(), resetKey);
        return resetKey;
    }

    public void resetPassword(PasswordResetRequest request, Errors errors) {
        if (errors.hasFieldErrors()) {
            throw new RestException(BAD_REQUEST, FormValidatorHelper.returnFormattedErrors(errors));
        }

        var user = userRepository.findByPasswordResetKey(request.getKey())
               .orElseThrow(() -> new RestException(NOT_FOUND, "Invalid password reset key"));

        var resetKey = PASSWORD_RESET_KEY_CODE_CACHE.get(user.getEmail());
        if (resetKey == null) {
            throw new RestException(UNAUTHORIZED, "Password reset key has expired");
        }

       user.setPassword(passwordEncoder.encode(request.getPassword()));
       userRepository.save(user);
    }
}
