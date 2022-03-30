package umcs.spotify.services;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import umcs.spotify.contract.AuthenticationCredentials;
import umcs.spotify.contract.AuthenticationResponse;
import umcs.spotify.contract.RegisterUserRequest;
import umcs.spotify.dto.UserDto;
import umcs.spotify.entity.RoleType;
import umcs.spotify.entity.User;
import umcs.spotify.exception.EmailAlreadyTakenException;
import umcs.spotify.exception.InvalidCredentialsException;
import umcs.spotify.helper.FormValidatorHelper;
import umcs.spotify.repository.RoleRepository;
import umcs.spotify.repository.UserRepository;
import umcs.spotify.helper.JwtUtils;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final ModelMapper mapper;

    public AuthService(
        AuthenticationManager authenticationManager,
        UserDetailsServiceImpl userDetailsService,
        PasswordEncoder passwordEncoder,
        UserRepository userRepository,
        RoleRepository roleRepository,
        UserService userService,
        JwtUtils jwtUtils,
        ModelMapper mapper
    ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.mapper = mapper;
    }


    public AuthenticationResponse signIn(AuthenticationCredentials credentials) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword()));
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Bad credentials");
        }

        var userDetails = userDetailsService.loadUserByUsername(credentials.getEmail());
        var token = jwtUtils.generateToken(userDetails);
        var user = userService.findUserByEmail(credentials.getEmail());
        return new AuthenticationResponse(token, mapper.map(user, UserDto.class));
    }

    public AuthenticationResponse signUp(RegisterUserRequest request, Errors errors) {
        if (errors.hasFieldErrors()) {
            throw new InvalidCredentialsException(FormValidatorHelper.returnFormattedErrors(errors));
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyTakenException("Email {} is already taken", request.getEmail());
        }

        var user = new User();
        user.setDisplayName(request.getDisplayName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var userRole = roleRepository.getByName(RoleType.USER);

        user.addRole(userRole);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        userRepository.save(user);
        userService.sendEmailConfirmationCode(user);

        var userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        var token = jwtUtils.generateToken(userDetails);
        return new AuthenticationResponse(token, mapper.map(user, UserDto.class));
    }



}
