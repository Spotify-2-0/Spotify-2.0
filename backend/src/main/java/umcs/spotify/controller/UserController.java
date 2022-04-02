package umcs.spotify.controller;

import org.springframework.data.domain.Page;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import umcs.spotify.contract.EmailConfirmRequest;
import umcs.spotify.contract.UserActivityRequest;
import umcs.spotify.contract.UserExistsByEmail;
import umcs.spotify.dto.UserDto;
import umcs.spotify.entity.User;
import umcs.spotify.entity.UserActivityEntry;
import umcs.spotify.services.UserActivityService;
import umcs.spotify.services.UserService;

import java.awt.print.Pageable;
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
    public Page<UserActivityEntry> getUserActivity(UserActivityRequest request, Errors errors) {
        return userActivityService.getUserActivity(request, errors);
    }
}
