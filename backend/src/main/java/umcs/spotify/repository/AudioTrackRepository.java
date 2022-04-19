package umcs.spotify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umcs.spotify.entity.AudioTrack;

public interface AudioTrackRepository extends JpaRepository<AudioTrack, Long> { }
