package umcs.spotify.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import umcs.spotify.entity.GeoLocation;

import java.time.LocalDateTime;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserActivityEntryDto {
    Long id;
    LocalDateTime occurrenceDate;
    GeoLocation location;
    String ip;
    String activity;
}
