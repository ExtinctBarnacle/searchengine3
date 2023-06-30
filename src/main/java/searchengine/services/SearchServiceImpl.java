package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.model.*;

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

    List <PageResult> searchResultSet = new ArrayList<>();
    String keyWord = "";

    @Override
    public String getSearch(String textToSearch) {
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
            if (lemma.getFrequency() > 10) {
                //words.remove(entry.getKey());
                words.put(entry.getKey(), 0);
            } else {
                words.put(entry.getKey(), lemma.getFrequency());
                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
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
            if (entry.getValue() == 0) {
                continue;
            }
            if (entry.getValue() > 0 && !keyWordFound){
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
                System.out.println(entry.getKey() + " " + page.getPath());
            }
        }
        if (foundPages.size() == 0){
            System.out.println("Nothing found");
        } else {
            foundPages.forEach(System.out::println);
        }
        for (int i = 0; i < foundPages.size(); i++){
            List<Page> pages = pageRepository.getByPath(foundPages.get(i));
            Page page = pages.get(0);
            PageResult pageResult = new PageResult();
            pageResult.setUri(foundPages.get(i));
            pageResult.setSnippet(page.getContent());
            Document doc = Jsoup.parse(pageResult.getSnippet()); // SiteIndexingServiceImpl.loadPage(pageResult.getUri());
            pageResult.setSnippet(doc.text());
            pageResult.setTitle(doc.title());
            System.out.println(pageResult.getSnippet());
            int snippetStart = pageResult.getSnippet().indexOf(keyWord);
            String snippetTextBefore = snippetStart>50 ? pageResult.getSnippet().substring(snippetStart-50, snippetStart)
                    : pageResult.getSnippet().substring(0, snippetStart);
            String snippetTextAfter = (pageResult.getSnippet().length() - (snippetStart + keyWord.length()))>50
                    ? pageResult.getSnippet().substring(snippetStart + keyWord.length(), snippetStart+50)
                    : pageResult.getSnippet().substring(snippetStart + keyWord.length(), pageResult.getSnippet().length()-1);
            pageResult.setSnippet(snippetTextBefore.concat("<b>".concat(keyWord).concat("</b>").concat(snippetTextAfter)));

            List<IndexS> indeces = indexRepository.findByPageId(page.getId());
            float rankSum = 0;
            for (IndexS indexS : indeces){
                rankSum = rankSum + indexS.getRank();
            }
            pageResult.setRelevance(rankSum);
            searchResultSet.add(pageResult);
        }
        float maxRank = 0;
        for (PageResult pageResult : searchResultSet){
            if (pageResult.getRelevance() > maxRank){
                maxRank = pageResult.getRelevance();
            }
        }
        for (PageResult pageResult : searchResultSet){
            pageResult.setRelevance(pageResult.getRelevance()/maxRank);
        }
        searchResultSet.sort(new Comparator<PageResult>() {
            @Override
            public int compare(PageResult o1, PageResult o2) {
                return 0;
            }
        });
        for (PageResult pageResult : searchResultSet){
            System.out.println(pageResult.getUri() + " " + pageResult.getRelevance());
            System.out.println(pageResult.getTitle());
            System.out.println(pageResult.getSnippet());
        }

        return "ok";
    }
}
