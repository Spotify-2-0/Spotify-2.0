package umcs.spotify.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import umcs.spotify.entity.Role;
import umcs.spotify.repository.UserRepository;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        return User.withUsername(username)
                .password(user.getPassword())
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .disabled(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }

    private List<SimpleGrantedAuthority> getAuthorities(umcs.spotify.entity.User user) {
        return user.getRoles()
                .stream()
                .map(getRoleSimpleGrantedAuthority())
                .collect(Collectors.toList());
    }

    private Function<Role, SimpleGrantedAuthority> getRoleSimpleGrantedAuthority() {
        return role -> new SimpleGrantedAuthority(role.getName().name());
    }
}
