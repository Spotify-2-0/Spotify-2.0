package umcs.spotify.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AddTrackDataToSelectDto {
    Long id;

    String name;
}
