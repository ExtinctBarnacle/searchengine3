package searchengine.services;

import searchengine.dto.Response;

public interface SiteIndexingService {
    Response getIndex();
    Response StopIndexing();
    Response indexPage (String url);
}
