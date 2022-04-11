package umcs.spotify.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
@Table(name = "collections")
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private CollectionType type;

    @ManyToMany(mappedBy = "collections")
    private List<User> users;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "collections_tracks",
            joinColumns = @JoinColumn(name = "collection_id"),
            inverseJoinColumns = @JoinColumn(name = "track_id"))
    private List<AudioTrack> tracks;

    private String avatarPath;
}
