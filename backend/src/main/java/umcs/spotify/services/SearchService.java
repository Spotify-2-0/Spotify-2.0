package umcs.spotify.services;

import org.springframework.stereotype.Service;
import umcs.spotify.contract.SearchResponse;
import umcs.spotify.dto.AudioTrackDto;
import umcs.spotify.dto.UserDto;
import umcs.spotify.helper.Mapper;
import umcs.spotify.repository.AudioTrackRepository;
import umcs.spotify.repository.UserRepository;

import java.util.ArrayList;

@Service
public class SearchService {

    private final AudioTrackRepository audioTrackRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    public SearchService(AudioTrackRepository audioTrackRepository, UserRepository userRepository, Mapper mapper) {
        this.audioTrackRepository = audioTrackRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public SearchResponse search(String searchText) {
        var artists = userRepository.findByDisplayNameContaining(searchText);
        var audioTracks = audioTrackRepository.findByNameContaining(searchText);

        return new SearchResponse(
                artists.stream()
                        .map(t -> mapper.map(t, UserDto.class))
                        .toList(),
                audioTracks.stream()
                        .map(t -> mapper.map(t, AudioTrackDto.class))
                        .toList());
    }
}
