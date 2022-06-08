package umcs.spotify.contract;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import umcs.spotify.dto.UserDto;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AuthenticationResponse {
    String type = "Bearer";
    String token;
    UserDto user;
}
