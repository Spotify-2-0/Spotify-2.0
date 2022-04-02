package umcs.spotify.services;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import umcs.spotify.contract.AuthenticationCredentials;
import umcs.spotify.contract.AuthenticationResponse;
import umcs.spotify.contract.RegisterUserRequest;
import umcs.spotify.dto.UserDto;
import umcs.spotify.entity.RoleType;
import umcs.spotify.entity.User;
import umcs.spotify.entity.UserActivityEntry;
import umcs.spotify.exception.RestException;
import umcs.spotify.helper.ContextUserAccessor;
import umcs.spotify.helper.FormValidatorHelper;
import umcs.spotify.helper.Mapper;
import umcs.spotify.repository.RoleRepository;
import umcs.spotify.repository.UserRepository;
import umcs.spotify.helper.JwtUtils;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.*;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserActivityService userActivityService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final Mapper mapper;

    public AuthService(
        AuthenticationManager authenticationManager,
        UserDetailsServiceImpl userDetailsService,
        UserActivityService userActivityService,
        PasswordEncoder passwordEncoder,
        UserRepository userRepository,
        RoleRepository roleRepository,
        UserService userService,
        JwtUtils jwtUtils,
        Mapper mapper
    ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userActivityService = userActivityService;
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
            throw new RestException(FORBIDDEN, "Bad credentials");
        }

        var userDetails = userDetailsService.loadUserByUsername(credentials.getEmail());
        var token = jwtUtils.generateToken(userDetails);
        var user = userService.findUserByEmail(credentials.getEmail());

        userActivityService.addActivity(user,
            "Sign in success",
            ContextUserAccessor.getRemoteAddres()
        );

        return new AuthenticationResponse(token, mapper.map(user, UserDto.class));
    }

    public AuthenticationResponse signUp(RegisterUserRequest request, Errors errors) {
        if (errors.hasFieldErrors()) {
            throw new RestException(BAD_REQUEST, FormValidatorHelper.returnFormattedErrors(errors));
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RestException(CONFLICT, "Email {} is already taken", request.getEmail());
        }

        var user = new User();
        user.setDisplayName(request.getDisplayName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var userRole = roleRepository.getByName(RoleType.USER);

        user.addRole(userRole);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        userService.assignDefaultAvatar(user);
        userRepository.save(user);
        userService.sendEmailConfirmationCode(user);

        var userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        var token = jwtUtils.generateToken(userDetails);
        return new AuthenticationResponse(token, mapper.map(user, UserDto.class));
    }



}
