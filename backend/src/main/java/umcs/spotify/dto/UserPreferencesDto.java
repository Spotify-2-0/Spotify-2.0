package umcs.spotify.dto;

import lombok.*;

import javax.validation.constraints.Size;

@Data
public class UserPreferencesDto {
    String displayName;
    String firstName;
    String lastName;
}
