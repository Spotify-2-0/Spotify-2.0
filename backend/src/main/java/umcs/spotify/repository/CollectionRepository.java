package umcs.spotify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umcs.spotify.entity.Collection;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
}
