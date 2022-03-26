package umcs.spotify.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import umcs.spotify.contract.AuthenticationCredentials;
import umcs.spotify.contract.AuthenticationResponse;
import umcs.spotify.security.JwtUtils;
import umcs.spotify.services.UserDetailsServiceImpl;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping(value = "/auth")
    public ResponseEntity<?> generateToken(@RequestBody AuthenticationCredentials credentials) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));

        var userDetails = userDetailsService.loadUserByUsername(credentials.getUsername());
        var token = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(token));
    }
}
