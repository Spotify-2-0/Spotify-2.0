package umcs.spotify.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umcs.spotify.contract.AddTrackRequest;
import umcs.spotify.contract.CollectionCreateRequest;
import umcs.spotify.dto.AudioTrackDto;
import umcs.spotify.dto.CollectionDto;
import umcs.spotify.dto.ErrorMessageDto;
import umcs.spotify.services.CollectionService;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/collection")
public class CollectionController {

    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping
    public List<CollectionDto> getCollections() {
        return collectionService.getCollections();
    }

    @GetMapping("/{id}")
    public CollectionDto getCollectionById(@PathVariable Long id) {
        return collectionService.getCollectionById(id);
    }

    @PostMapping
    public ResponseEntity<CollectionDto> addCollection(@Valid @RequestBody CollectionCreateRequest collectionCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collectionService.addCollection(collectionCreateRequest));
    }

    @PostMapping("/{collectionId}/track")
    public AudioTrackDto addTrackToCollection(@PathVariable long collectionId, @ModelAttribute AddTrackRequest addTrackRequest) {
        return collectionService.addTrack(collectionId, addTrackRequest);
    }

    @DeleteMapping("/{collectionId}/track/{trackId}")
    public void removeTrackFromCollection(@PathVariable long collectionId, @PathVariable long trackId) {
        collectionService.removeTrack(collectionId, trackId);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageDto> entityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(ex.getLocalizedMessage()));
    }

}
