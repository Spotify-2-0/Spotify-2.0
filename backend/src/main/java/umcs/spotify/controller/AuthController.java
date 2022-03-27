package umcs.spotify.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import umcs.spotify.contract.AuthenticationCredentials;
import umcs.spotify.contract.AuthenticationResponse;
import umcs.spotify.contract.RegisterUserRequest;
import umcs.spotify.entity.RoleType;
import umcs.spotify.entity.User;
import umcs.spotify.repository.RoleRepository;
import umcs.spotify.repository.UserRepository;
import umcs.spotify.security.JwtUtils;
import umcs.spotify.services.UserDetailsServiceImpl;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> generateToken(@RequestBody AuthenticationCredentials credentials) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Bad credentials");
        }

        var userDetails = userDetailsService.loadUserByUsername(credentials.getUsername());
        var token = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserRequest userData) {
        if (userRepository.existsByUsername((userData.getUsername()))) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        if (!userData.getPassword().equals(userData.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }

        var user = new User(
                userData.getUsername(),
                userData.getEmail(),
                passwordEncoder.encode(userData.getPassword()));
        var userRole = roleRepository.findByName(RoleType.USER).get();
        user.addRole(userRole);

        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        user.setBirthdate(userData.getBirthdate());

        userRepository.save(user);

        return ResponseEntity.ok("Account created");
    }
}
