package umcs.spotify.dto;

import lombok.*;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class GenreDto {
    Long id;
    String name;
}
