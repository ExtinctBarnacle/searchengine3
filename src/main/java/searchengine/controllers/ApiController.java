package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.*;

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

        return ResponseEntity.ok(siteIndexingService.StopIndexing());
    }
    @PostMapping ("/indexPage")
    @ResponseBody
    public ResponseEntity indexPage(@RequestParam(name = "url") String url) {

        return ResponseEntity.ok(siteIndexingService);
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity search(@RequestParam(name = "query") String query) {

        return ResponseEntity.ok(searchService.getSearch(query));
    }
}