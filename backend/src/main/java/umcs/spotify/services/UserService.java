package umcs.spotify.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import umcs.spotify.dto.UserDto;
import umcs.spotify.dto.UserPreferencesDto;
import umcs.spotify.entity.User;
import umcs.spotify.exception.RestException;
import umcs.spotify.helper.ContextUserAccessor;
import umcs.spotify.helper.PinCodeHelper;
import umcs.spotify.repository.UserRepository;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ModelMapper mapper;

    public UserService(
            UserRepository userRepository,
            EmailService emailService,
            ModelMapper mapper
    ) {
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

    public void changePreferences(String displayName, String firstName, String lastName) {
        var currentUser = findUserByEmail(ContextUserAccessor.getCurrentUserEmail());
        currentUser.setDisplayName(displayName);
        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        userRepository.save(currentUser);
    }

    public UserPreferencesDto getPreferences() {
        var email = ContextUserAccessor.getCurrentUserEmail();
        var currentUser = findUserByEmail(email);
        return mapper.map(currentUser, UserPreferencesDto.class);
    }
}
