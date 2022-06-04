package umcs.spotify.controller;

import org.springframework.web.bind.annotation.*;
import umcs.spotify.contract.SearchResponse;
import umcs.spotify.services.SearchService;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public SearchResponse search(@RequestParam("q") String searchText) {
        return searchService.search(searchText);
    }
}
