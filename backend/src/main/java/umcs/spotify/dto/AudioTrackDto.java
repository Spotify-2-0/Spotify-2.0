package umcs.spotify.dto;

import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AudioTrackDto {
    Long id;
    String name;
    Duration duration;
    String filePath;
    Long views;
    LocalDateTime publishedDate;
    List<GenreDto> genres;
}
