package umcs.spotify.contract;

import lombok.*;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserExistsByEmail {
    String email;
}
