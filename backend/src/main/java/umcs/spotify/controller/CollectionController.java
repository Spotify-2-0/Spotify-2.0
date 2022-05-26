package umcs.spotify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umcs.spotify.contract.AddTrackRequest;
import umcs.spotify.dto.AddTrackDataToSelectDto;
import umcs.spotify.contract.CollectionCreateRequest;
import umcs.spotify.dto.AudioTrackDto;
import umcs.spotify.contract.UpdateCollectionRequest;
import umcs.spotify.dto.CollectionDto;
import umcs.spotify.dto.ErrorMessageDto;
import umcs.spotify.entity.CollectionType;
import umcs.spotify.services.CollectionService;
import umcs.spotify.services.FavouritesService;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/collection")
public class CollectionController {

    private final CollectionService collectionService;
    private final FavouritesService favouritesService;

    public CollectionController(CollectionService collectionService, FavouritesService favouritesService) {
        this.collectionService = collectionService;
        this.favouritesService = favouritesService;
    }

    @GetMapping
    public List<CollectionDto> getCollections() {
        return collectionService.getCollections();
    }

    @GetMapping("/me")
    public List<CollectionDto> getUserCollections() {
        return collectionService.getUserCollections();
    }

    @GetMapping("/loggedUser")
    public ResponseEntity<List<AddTrackDataToSelectDto>> getUserLoggedCollections() {
        return ResponseEntity.ok(collectionService.getUserLoggedCollections());
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
    public ResponseEntity<List<CollectionType>> getCollectionsTypes() {
        return ResponseEntity.ok(this.collectionService.getCollectionTypes());
    }

    @PostMapping
    public ResponseEntity<CollectionDto> addCollection(@Valid @ModelAttribute CollectionCreateRequest collectionCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collectionService.addCollection(collectionCreateRequest));
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
    public ResponseEntity<AudioTrackDto> addTrackToCollection(
            @PathVariable long collectionId,
            @ModelAttribute AddTrackRequest addTrackRequest) {
        return ResponseEntity.ok(collectionService.addTrack(collectionId, addTrackRequest));
    }

    @GetMapping("/favourites")
    public ResponseEntity<CollectionDto> getFavourites() {
        return ResponseEntity.ok(favouritesService.getFavourites());
    }

    @PostMapping("/favourites/{trackId}")
    public ResponseEntity<CollectionDto> addTrackToFavourites(@PathVariable long trackId) {
        return ResponseEntity.ok(favouritesService.addTrackToFavourites(trackId));
    }

    @DeleteMapping("/favourites/{trackId}")
    public void removeTrackFromFavourites(@PathVariable long trackId) {
        favouritesService.removeTrackFromFavourites(trackId);
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
