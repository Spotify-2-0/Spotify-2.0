package umcs.spotify.dto;

import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String displayName;
    private String email;
    private boolean emailConfirmed;

}
