package umcs.spotify.contract;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import umcs.spotify.entity.CollectionType;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UpdateCollectionRequest {
    String name;
    CollectionType type;
    String avatarPath;
}
