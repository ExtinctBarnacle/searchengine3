package searchengine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.model.SiteRepository;
import searchengine.services.SearchService;
import searchengine.services.SiteIndexingService;
import searchengine.services.SiteIndexingServiceImpl;
import searchengine.services.StatisticsService;

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
    public String startIndexing(){

        return siteIndexingService.getIndex();
    }
    /*@GetMapping("/search/{query}/{site}/{offset}/{limit}")
    public String userSearch(@PathVariable("query") String query){
        return searchService.getSearch(query);
    }*/
    @GetMapping("/search")
    @ResponseBody
    public String search(@RequestParam(name = "query") String query){
        return searchService.getSearch(query);
    }
}