package umcs.spotify.contract;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
public class RegisterUserRequest {
    @Size(min = 5, max = 20)
    private String username;

    @Email
    private String email;

    @Size(min = 6, max = 40)
    private String password;
}
