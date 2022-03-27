package umcs.spotify.contract;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class RegisterUserRequest {
    @Size(min = 5, max = 20)
    private String username;

    @Email
    private String email;

    @Size(min = 6, max = 40)
    private String password;

    @Size(min = 6, max = 40)
    private String confirmPassword;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    private LocalDate birthdate;
}
