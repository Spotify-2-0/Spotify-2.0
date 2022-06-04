package umcs.spotify.contract;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import umcs.spotify.dto.AudioTrackDto;
import umcs.spotify.dto.UserDto;

import java.util.List;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class SearchResponse {
    private List<UserDto> artists;
    private  List<AudioTrackDto> audioTracks;
}
