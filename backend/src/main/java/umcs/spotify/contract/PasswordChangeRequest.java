package umcs.spotify.contract;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.Pattern;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PasswordChangeRequest {
    String oldPassword;
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#&()–{}:;',?/*~$^+=<>]).{8,255}$",
            message = "Password must be between 8 to 255 characters long, contain uppercase letter, lowercase letter, number, and special character"
    )
    String newPassword;
    String repeatedNewPassword;
}
