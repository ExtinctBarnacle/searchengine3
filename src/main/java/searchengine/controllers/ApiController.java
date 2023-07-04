package searchengine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.model.SiteRepository;
import searchengine.services.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final StatisticsService statisticsService;
    private final SiteIndexingService siteIndexingService;
    private final SearchService searchService;

    public ApiController(StatisticsService statisticsService, SiteIndexingService siteIndexingService, SearchService searchService) {

        this.statisticsService = statisticsService;
        this.siteIndexingService = siteIndexingService;
        this.searchService = searchService;

    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity startIndexing() {

        return ResponseEntity.ok(siteIndexingService.getIndex());
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity stopIndexing() {


        return ResponseEntity.ok(siteIndexingService);
    }

    /*@GetMapping("/search/{query}/{site}/{offset}/{limit}")
    public String userSearch(@PathVariable("query") String query){
        return searchService.getSearch(query);
    }*/
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity search(@RequestParam(name = "query") String query) {
        return ResponseEntity.ok(searchService.getSearch(query));
    }
}