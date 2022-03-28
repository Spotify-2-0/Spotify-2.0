package umcs.spotify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import umcs.spotify.entity.Role;
import umcs.spotify.entity.RoleType;
import umcs.spotify.repository.RoleRepository;

import java.util.Arrays;

@Component
public class DataSeed implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        Arrays.stream(RoleType.values()).forEach(this::createRoleIfNotExists);
    }

    private void createRoleIfNotExists(RoleType roleType) {
        if (roleRepository.findByName(roleType).isEmpty()) {
            roleRepository.save(new Role(roleType));
        }
    }
}
