package umcs.spotify.dto;

import lombok.*;
import umcs.spotify.entity.GeoLocation;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserActivityEntryDto {
    Long id;
    Long occurrenceDate;
    GeoLocation location;
    String ip;
    String activity;
}
