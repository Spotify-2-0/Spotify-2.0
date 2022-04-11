package umcs.spotify.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umcs.spotify.contract.CollectionRequest;
import umcs.spotify.contract.UpdateCollectionRequest;
import umcs.spotify.dto.CollectionDto;
import umcs.spotify.dto.ErrorMessageDto;
import umcs.spotify.entity.Collection;
import umcs.spotify.services.CollectionService;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/collections")
public class CollectionController {

    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping("/")
    public List<CollectionDto> getCollections() {
        return collectionService.getCollections();
    }

    @GetMapping("/{id}")
    public CollectionDto getCollectionById(@PathVariable Long id) {
        return collectionService.getCollectionById(id);
    }

    @PostMapping("/")
    public ResponseEntity<CollectionDto> addCollection(@Valid @RequestBody CollectionRequest collectionRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collectionService.addCollection(collectionRequest));
    }

    @PutMapping("/{id}")
    public void updateCollection(@PathVariable Long id, @Valid @RequestBody UpdateCollectionRequest request) {
        collectionService.updateCollection(id, request.getName(), request.getType(), request.getAvatarPath());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageDto> entityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(ex.getLocalizedMessage()));
    }
}
