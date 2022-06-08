package umcs.spotify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umcs.spotify.entity.Role;
import umcs.spotify.entity.RoleType;
import umcs.spotify.entity.User;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleType name);
    Role getByName(RoleType name);
}
