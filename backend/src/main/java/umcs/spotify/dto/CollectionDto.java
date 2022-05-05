package umcs.spotify.dto;

import lombok.*;
import umcs.spotify.entity.CollectionType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CollectionDto {
    Long id;
    String name;
    CollectionType type;
    String imageMongoRef;
    Long duration;
    Long views;
    LocalDateTime publishedDate;
    List<AudioTrackDto> tracks;
    List<UserDto> users;
}
