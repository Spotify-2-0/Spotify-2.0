package umcs.spotify.contract;

import lombok.Value;

@Value
public class AuthenticationResponse {
    private String token;
    private String type = "Bearer";

    public AuthenticationResponse(String token) {
        this.token = token;
    }
}
