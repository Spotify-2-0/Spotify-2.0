package umcs.spotify.dto;

import lombok.*;
import umcs.spotify.entity.GeoLocation;

import java.time.LocalDateTime;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserActivityEntryDto {
    Long id;
    LocalDateTime occurrenceDate;
    GeoLocation location;
    String ip;
    String activity;
}
