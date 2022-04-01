package umcs.spotify.controller;

import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import umcs.spotify.contract.*;
import umcs.spotify.dto.UserDto;
import umcs.spotify.services.UserService;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
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
