package umcs.spotify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umcs.spotify.entity.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> { }
