package umcs.spotify.entity;

import lombok.*;

import javax.persistence.Embeddable;

@NoArgsConstructor
@Data
@Embeddable
public class GeoLocation {
    public String city;
    public String country;
    public String continent;
    public Double latitude;
    public Double longitude;
}
