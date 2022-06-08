package umcs.spotify.dto;

import lombok.*;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserDto {

    Long id;
    String firstName;
    String lastName;
    String displayName;
    String email;
    boolean emailConfirmed;

}
