package searchengine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.model.SiteRepository;
import searchengine.services.SiteIndexingService;
import searchengine.services.SiteIndexingServiceImpl;
import searchengine.services.StatisticsService;

@RestController
@RequestMapping("/api")
public class ApiController {
    @Autowired
    private SiteRepository siteRepository;

    private final StatisticsService statisticsService;
    private final SiteIndexingService siteIndexingService;

    public ApiController(StatisticsService statisticsService, SiteIndexingService siteIndexingService) {

        this.statisticsService = statisticsService;
        this.siteIndexingService = siteIndexingService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public String startIndexing(){

        return siteIndexingService.getIndex();
    }
}