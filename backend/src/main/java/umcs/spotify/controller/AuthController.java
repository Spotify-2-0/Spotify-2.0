package umcs.spotify.controller;

import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umcs.spotify.contract.AuthenticationCredentials;
import umcs.spotify.contract.AuthenticationResponse;
import umcs.spotify.contract.RegisterUserRequest;
import umcs.spotify.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    public AuthenticationResponse generateToken(@RequestBody AuthenticationCredentials credentials) {
        return authService.signIn(credentials);
    }

    @PostMapping("/signup")
    public AuthenticationResponse registerUser(@Validated @RequestBody RegisterUserRequest userData, Errors errors) {
        return authService.signUp(userData, errors);
    }
}
