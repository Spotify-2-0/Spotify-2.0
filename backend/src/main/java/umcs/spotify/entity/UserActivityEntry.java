package umcs.spotify.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
@Table(name = "user_activity")
public class UserActivityEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    public LocalDateTime occurrenceDate;
    @Embedded
    public GeoLocation location;
    public String ip;
    public String activity;

}
