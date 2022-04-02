package umcs.spotify.controller;

import org.springframework.web.bind.annotation.*;
import umcs.spotify.contract.ChangeUserPreferencesRequest;
import umcs.spotify.contract.EmailConfirmRequest;
import umcs.spotify.contract.UserExistsByEmail;
import umcs.spotify.dto.UserPreferencesDto;
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

    @PostMapping("/updatePreferences")
    public void updatePreferences(@RequestBody ChangeUserPreferencesRequest request) {
        userService.changePreferences(request.getEmail(), request.getFirstName(), request.getLastName());
    }

    @GetMapping("/getPreferences")
    public UserPreferencesDto getPreferences() {
        return userService.getPreferences();
    }
}
