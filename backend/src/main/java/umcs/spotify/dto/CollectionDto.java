package umcs.spotify.dto;

import lombok.*;
import umcs.spotify.entity.CollectionType;

import java.util.List;

@Data
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CollectionDto {
    Long id;
    String name;
    CollectionType type;
    String avatarPath;
    List<AudioTrackDto> tracks;
    List<UserDto> users;
}
