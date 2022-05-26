package umcs.spotify.services;

import org.springframework.stereotype.Service;
import umcs.spotify.entity.Genre;
import umcs.spotify.repository.GenreRepository;

import java.util.List;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(final GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<Genre> getGenres() {
        return this.genreRepository.findAll();
    }

    public List<Genre> findAllStartingWith(final String str) {
        return this.genreRepository.findByNameStartingWithIgnoreCase(str);
    }
}
