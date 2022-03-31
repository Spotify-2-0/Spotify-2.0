package umcs.spotify.contract;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.*;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RegisterUserRequest {

    @Size(min = 3, max = 16,
        message = "Display name length must be between 3 and 16 characters")
    String displayName;

    @Email(message = "Email must be in format username@domain.com")
    String email;

    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#&()–{}:;',?/*~$^+=<>]).{8,255}$",
        message = "Password must be between 8 to 255 characters long, contain uppercase letter, lowercase letter, number, and special character"
    )
    String password;

    @Size(min = 1, max = 16,
            message = "First name must be between 1 and 16 characters")
    String firstName;

    @Size(min = 1, max = 16,
            message = "Last name must be between 1 and 16 characters")
    String lastName;
}
