package umcs.spotify.contract;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import umcs.spotify.entity.CollectionType;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CollectionRequest {
    String name;
    CollectionType type;
}
