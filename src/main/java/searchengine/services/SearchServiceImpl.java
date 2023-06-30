package searchengine.services;

import lombok.RequiredArgsConstructor;
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
        for (Map.Entry<String, Integer> entry : sortedWords.entrySet()) {
            if (entry.getValue() == 0) {
                continue;
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
        
        //System.out.println("here");
        return "ok";
    }
}
