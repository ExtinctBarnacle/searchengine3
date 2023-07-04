package searchengine.services;

import searchengine.dto.PageResult;
import searchengine.dto.PagesResponse;

import java.util.List;

public interface SearchService {
    PagesResponse getSearch(String textToSearch);
}
