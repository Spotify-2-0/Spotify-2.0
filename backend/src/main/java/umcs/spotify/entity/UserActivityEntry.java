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
    private long occurrenceTimestamp;
    @Embedded
    private GeoLocation location;
    private String ip;
    private String activity;

}
