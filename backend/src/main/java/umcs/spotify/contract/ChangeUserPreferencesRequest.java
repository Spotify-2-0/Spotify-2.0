package umcs.spotify.contract;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ChangeUserPreferencesRequest {

    @Size(min = 3, max = 16,
            message = "Display name length must be between 3 and 16 characters")
    String displayName;

    @Size(min = 1, max = 16,
            message = "First name must be between 1 and 16 characters")
    String firstName;

    @Size(min = 1, max = 16,
            message = "Last name must be between 1 and 16 characters")
    String lastName;
}
