package umcs.spotify.contract;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;
import umcs.spotify.entity.CollectionType;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CollectionCreateRequest {
    String name;
    MultipartFile image;
    CollectionType type;
}
