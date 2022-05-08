package umcs.spotify.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umcs.spotify.contract.AddTrackRequest;
import umcs.spotify.contract.AddTrackToCollectionRequest;
import umcs.spotify.contract.CollectionCreateRequest;
import umcs.spotify.dto.AudioTrackDto;
import umcs.spotify.contract.UpdateCollectionRequest;
import umcs.spotify.dto.CollectionDto;
import umcs.spotify.dto.ErrorMessageDto;
import umcs.spotify.entity.CollectionType;
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

    @GetMapping("/{id}/avatar")
    public ResponseEntity<InputStreamResource> getCollectionAvatar(@PathVariable long id) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(collectionService.getCollectionAvatar(id));
    }

    @GetMapping("/types")
    public ResponseEntity<CollectionType[]> getCollectionsTypes() {
        return ResponseEntity.ok(this.collectionService.getCollectionTypes());
    }

    @PostMapping
    public ResponseEntity<CollectionDto> addCollection(@Valid @ModelAttribute CollectionCreateRequest collectionCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collectionService.addCollection(collectionCreateRequest));
    }

    @PostMapping("/{collectionId}/addVisit")
    public void addVisitToCollection(@PathVariable long collectionId) {
        collectionService.addVisitToCollection(collectionId);
    }

    @PostMapping("/{collectionId}/follow")
    public void followCollection(@PathVariable long collectionId) {
        collectionService.followCollection(collectionId);
    }

    @PostMapping("/{collectionId}/unfollow")
    public void unfollowCollection(@PathVariable long collectionId) {
        collectionService.unfollowCollection(collectionId);
    }

    @PostMapping("/{collectionId}/track")
    public ResponseEntity<AudioTrackDto> addTrackToCollection(@PathVariable long collectionId, @ModelAttribute AddTrackRequest addTrackRequest) {
        return ResponseEntity.ok(collectionService.addTrack(collectionId, addTrackRequest));
    }

    @DeleteMapping("/{collectionId}/track/{trackId}")
    public void removeTrackFromCollection(@PathVariable long collectionId, @PathVariable long trackId) {
        collectionService.removeTrack(collectionId, trackId);
    }

    @PutMapping("/{id}")
    public void updateCollection(@PathVariable Long id, @Valid @RequestBody UpdateCollectionRequest request) {
        collectionService.updateCollection(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteCollection(@PathVariable Long id) {
        collectionService.deleteCollection(id);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageDto> entityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(ex.getLocalizedMessage()));
    }
}
