package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.dto.PageResult;
import searchengine.dto.PagesResponse;
import searchengine.model.*;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    HashMap<String, Integer> words = new HashMap<>();
    private final LemmaRepository lemmaRepository;
    private final PageRepository pageRepository;
    private final IndexRepository indexRepository;
    private final SiteRepository siteRepository;

    List<PageResult> searchResultSet = new ArrayList<>();
    String keyWord = "";

    @Override
    public PagesResponse getSearch(String textToSearch) {
        try {
            words = Lemmatizator.getWordsCount(textToSearch);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Map.Entry<String, Integer> entry : words.entrySet()) {
            List<Lemma> lemmas = lemmaRepository.findByLemma(entry.getKey());
            if (lemmas.size() == 0) {
                //words.remove(entry.getKey());
                words.put(entry.getKey(), 0);
                continue;
            }
            Lemma lemma = lemmas.get(0);
            if (lemma.getFrequency() > 100) {
                //words.remove(entry.getKey());
                words.put(entry.getKey(), 0);
            } else {
                words.put(entry.getKey(), lemma.getFrequency());

            }
        }
        Map<String, Integer> sortedWords = words.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors
                        .toMap(Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1,
                                LinkedHashMap::new));
        sortedWords.entrySet().forEach(System.out::println);
        List<String> foundPages = new ArrayList<>();
        boolean keyWordFound = false;
        for (Map.Entry<String, Integer> entry : sortedWords.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            if (entry.getValue() == 0) {
                continue;
            }
            if (entry.getValue() > 0 && !keyWordFound) {
                keyWord = entry.getKey();
                keyWordFound = !keyWordFound;
            }
            List<Lemma> lemmas = lemmaRepository.findByLemma(entry.getKey());
            Lemma lemma = lemmas.get(0);
            List<IndexS> indeces = indexRepository.findByLemmaId(lemma.getId());
            for (IndexS indexS : indeces) {
                List<Page> pages = pageRepository.getByid(indexS.getPageId());
                if (pages.size() == 0) {
                    continue;
                }
                Page page = pages.get(0);
                boolean pageContainsLemma = false;
                for (int i = 0; i < foundPages.size(); i++) {
                    if (foundPages.get(i).equals(page.getPath())) {
                        pageContainsLemma = !pageContainsLemma;
                    }
                }
                if (!pageContainsLemma) {
                    foundPages.remove(page.getPath());
                }
                foundPages.add(page.getPath());
                //System.out.println(entry.getKey() + " " + page.getPath());
            }
        }
        if (foundPages.size() == 0) {
            System.out.println("Nothing found");
        }
        for (int i = 0; i < foundPages.size(); i++) {
            List<Page> pages = pageRepository.getByPath(foundPages.get(i));
            Page page = pages.get(0);
            PageResult pageResult = new PageResult();
            //SiteIndexingServiceImpl a = new SiteIndexingServiceImpl();
            int start = foundPages.get(i).indexOf("//") + 2;
            int end = foundPages.get(i).indexOf("/", start);
            if (end == -1) {end = foundPages.get(i).length();}
            pageResult.setSite(foundPages.get(i).substring(0, end));
            List<Site> sites = siteRepository.findByUrl(pageResult.getSite()+"/");
            Site site = sites.get(0);
            pageResult.setSiteName(site.getName());
            pageResult.setUri(foundPages.get(i));
            pageResult.setSnippet(page.getContent());
            Document doc = Jsoup.parse(pageResult.getSnippet()); // SiteIndexingServiceImpl.loadPage(pageResult.getUri());
            pageResult.setSnippet(doc.text());
            pageResult.setTitle(doc.title());
            for (Map.Entry<String, Integer> entry : sortedWords.entrySet()) {
                keyWord = entry.getKey();
                pageResult.setSnippet(pageResult.getSnippet().replaceAll(keyWord, "<b>".concat(keyWord).concat("</b>")));
            }
            int keyWordStart = pageResult.getSnippet().indexOf("<b>");
            int snippetStart = keyWordStart > 150 ? keyWordStart - 150 : 0;
            int snippetEnd = (pageResult.getSnippet().length() - (keyWordStart + 3)) > 150 ? keyWordStart + 150 : pageResult.getSnippet().length() - 1;
            pageResult.setSnippet(pageResult.getSnippet().substring(snippetStart, snippetEnd));

            List<IndexS> indeces = indexRepository.findByPageId(page.getId());
            float rankSum = 0;
            for (IndexS indexS : indeces) {
                rankSum = rankSum + indexS.getRank();
            }
            pageResult.setRelevance(rankSum);
            searchResultSet.add(pageResult);
        }
        float maxRank = 0;
        for (PageResult pageResult : searchResultSet) {
            if (pageResult.getRelevance() > maxRank) {
                maxRank = pageResult.getRelevance();
            }
        }
        for (PageResult pageResult : searchResultSet) {
            pageResult.setRelevance(pageResult.getRelevance() / maxRank);
        }
        Collections.sort(searchResultSet, Comparator.reverseOrder());

        for (PageResult pageResult : searchResultSet) {
        }
        PagesResponse pagesResponse = new PagesResponse();
        pagesResponse.setData(searchResultSet);
        pagesResponse.setResult(true);
        pagesResponse.setCount(searchResultSet.size());
        return pagesResponse;
    }
}
