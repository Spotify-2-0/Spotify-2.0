package umcs.spotify.contract;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AddTrackRequest {
    MultipartFile track;
    String name;
    List<Long> genres;
    List<Long> artists;
}
