package umcs.spotify.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umcs.spotify.services.TrackService;

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
}