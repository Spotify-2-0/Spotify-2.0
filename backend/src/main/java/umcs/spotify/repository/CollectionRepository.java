package umcs.spotify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umcs.spotify.entity.Collection;
import umcs.spotify.entity.CollectionType;

import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    Optional<Collection> findByNameAndType(String name, CollectionType type);
}
