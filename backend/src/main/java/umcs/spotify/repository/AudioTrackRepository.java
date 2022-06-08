package umcs.spotify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umcs.spotify.entity.AudioTrack;

import java.util.List;

public interface AudioTrackRepository extends JpaRepository<AudioTrack, Long> {
    List<AudioTrack> findByNameContaining(String searchText);
}
