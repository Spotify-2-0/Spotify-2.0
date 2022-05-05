package umcs.spotify.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umcs.spotify.contract.AddTrackRequest;
import umcs.spotify.dto.AudioTrackDto;
import umcs.spotify.dto.ErrorMessageDto;
import umcs.spotify.repository.GenreRepository;
import umcs.spotify.repository.UserRepository;
import umcs.spotify.services.TrackService;
import umcs.spotify.services.UserService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/track")
public class TrackController {

    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> streamAudio(
            @PathVariable String id,
            @RequestParam("token") String token,
            @RequestHeader("Range") String range
    ) {
        return trackService.getTrackChunked(id, token, range);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<AudioTrackDto> getAudioTrackDetails(@PathVariable Long id) {
        return ResponseEntity.ok(trackService.getAudioTrackDetails(id));
    }
    @GetMapping
    public ResponseEntity<List<AudioTrackDto>> getTracks(@RequestParam(required = false) Long collectionId) {
        return ResponseEntity.ok(trackService.getTracks(collectionId));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageDto> entityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(ex.getLocalizedMessage()));
    }
}