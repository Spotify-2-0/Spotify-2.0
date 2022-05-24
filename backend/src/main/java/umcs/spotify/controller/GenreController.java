package umcs.spotify.controller;

import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umcs.spotify.entity.Genre;
import umcs.spotify.services.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genre")
public class GenreController {

    private final GenreService genreService;

    public GenreController(final GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<List<Genre>> getGenres() {
        return ResponseEntity.ok(this.genreService.getGenres());
    }

    @GetMapping("/startingWith")
    public ResponseEntity<List<Genre>> getGenresStartingWith(@Param("name") final String name) {
        return ResponseEntity.ok(this.genreService.findAllStartingWith(name));
    }
}
