package umcs.spotify.entity;

import lombok.*;

import javax.persistence.Embeddable;

@NoArgsConstructor
@Data
@Embeddable
public class GeoLocation {
    private String city;
    private String country;
    private String continent;
    private Double latitude;
    private Double longitude;
    private Integer radius;
}
