package umcs.spotify.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import umcs.spotify.contract.ChangeUserPreferencesRequest;
import umcs.spotify.contract.EmailConfirmRequest;
import umcs.spotify.contract.UserExistsByEmail;
import umcs.spotify.dto.UserPreferencesDto;
import org.springframework.web.multipart.MultipartFile;
import umcs.spotify.contract.*;
import umcs.spotify.dto.UserActivityEntryDto;
import umcs.spotify.dto.UserDto;
import umcs.spotify.services.UserActivityService;
import umcs.spotify.services.UserService;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserActivityService userActivityService;
    private final UserService userService;

    public UserController(
        UserActivityService userActivityService,
        UserService userService
    ) {
        this.userActivityService = userActivityService;
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserDto getCurrentUser() {
        return userService.getCurrentUser();
    }

    @PostMapping("/existsByEmail")
    public Map<String, Boolean> existsByEmail(@RequestBody UserExistsByEmail request) {
        return Collections.singletonMap("exists", userService.existsByEmail(request.getEmail()));
    }

    @PostMapping("/confirmEmail")
    public Map<String, Boolean> confirmEmail(@RequestBody EmailConfirmRequest request) {
        return Collections.singletonMap("success", userService.confirmEmail(request.getCode()));
    }

    @PostMapping("/sendEmailConfirmationCode")
    public void sendEmailConfirmationCode() {
        userService.sendEmailConfirmationCodeForCurrentUser();
    }

    @GetMapping("/activity")
    public Page<UserActivityEntryDto> getUserActivity(UserActivityRequest request, Errors errors) {
        return userActivityService.getUserActivity(request, errors);
    }

    @PostMapping("/updatePreferences")
    public void updatePreferences(@RequestBody ChangeUserPreferencesRequest request) {
        userService.changePreferences(request.getDisplayName(), request.getFirstName(), request.getLastName());
    }

    @GetMapping("/getPreferences")
    public UserPreferencesDto getPreferences() {
        return userService.getPreferences();
    }

    @PostMapping("/uploadAvatar")
    public void uploadUserAvatar(@RequestParam("image") MultipartFile multipartFile) {
        userService.uploadAvatar(multipartFile);
    }

    @GetMapping("/profile/{id}/avatar")
    public ResponseEntity<InputStreamResource> getUserAvatar(@PathVariable long id) {
        return userService.getUserAvatar(id);
    }

    @PostMapping("/sendEmailPasswordReset")
    public void sendEmailPasswordReset(@RequestBody SendEmailPasswordResetRequest request) {
        userService.sendEmailPasswordReset(request.getEmail());
    }

    @PostMapping("/passwordResetKeyFromPinCode")
    public Map<String, String> getPasswordChangeKeyFromPinCode(@RequestBody PasswordResetPinToKeyRequest request) {
        return Collections.singletonMap("key", userService.getPasswordChangeKeyFromPinCode(request));
    }

    @PostMapping("/resetPassword")
    public void resetPassword(@Validated @RequestBody PasswordResetRequest request, Errors errors) {
        userService.resetPassword(request, errors);
    }
}
