package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import searchengine.model.*;
import searchengine.config.SitesList;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import static java.lang.Thread.sleep;

@Service
@RequiredArgsConstructor
//@Repository
public class SiteIndexingServiceImpl extends RecursiveAction implements SiteIndexingService {
    //static List<String> refsList = new ArrayList<>();
    //public static List<String> refstoFile = new ArrayList<>();
    private final SitesList sites;
    @Autowired
    private final PageRepository pageRepository;
    @Autowired
    private final SiteRepository siteRepository;
    @Autowired
    private final LemmaRepository lemmaRepository;
    @Autowired
    private final IndexRepository indexRepository;
    HashMap<String, Integer> words = new HashMap<>();
    String siteAddress;
    static String siteToIndex;
    Page page;
    Site site = new Site();
    Lemma lemma = new Lemma();
    IndexS indexS = new IndexS();

    public void StartIndexing(){
        ForkJoinPool pool = new ForkJoinPool();
        //SitesList sites = new SitesList();
        //System.out.println(sites.getSites().size());

        for (searchengine.config.Site i : sites.getSites()) {
            //System.out.println();
            List <Site> curSites = siteRepository.findByUrl(i.getUrl());
            for (Site site : curSites){
                //siteRepository.deleteById(site.getId());
            }
            List <Page> curPages = pageRepository.findAll();
            for (Page page : curPages){
                String path = "https://".concat(getDomenName(page.getPath()));
                if (path.equals(i.getUrl())){
                    //pageRepository.deleteById(page.getId());
                }
            }
            site.setUrl(i.getUrl());
            site.setName(i.getName());
            site.setStatus(Status.INDEXING);
            site.setLastError(null);
            site.setStatusTime(Date.valueOf(LocalDate.now()));
            siteRepository.save(site);
            SiteIndexingServiceImpl index = new SiteIndexingServiceImpl(sites, pageRepository, siteRepository,lemmaRepository,indexRepository);
            index.siteAddress = i.getUrl();
            index.siteToIndex = i.getUrl();
            index.page = new Page();
            index.page.setSiteId(site.getId());
            index.page.setPath(index.siteAddress);
            index.compute();
            //index.refstoFile.add(i.getUrl());
            pool.invoke(index);
        }
    }
    @Override
    public String getIndex(){
        StartIndexing();
        return "ok";
    };
    @Override
    public void compute() {
        String domenName = getDomenName(siteAddress);
        String givenDomen = getDomenName(siteToIndex);
        if (!(domenName.equals(givenDomen))){return;}
        Document doc = loadPage(siteAddress);
        if (Objects.isNull(doc)){return;}
        List <Page> addedPages = pageRepository.getByPath(siteAddress);
        if (addedPages.size() == 0) {
            try {
                words = Lemmatizator.getWordsCount(Jsoup.parse(doc.toString()).text());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            page.setCode(doc.connection().response().statusCode());
            page.setContent(doc.toString());
            pageRepository.save(page);
            for (Map.Entry entry : words.entrySet()) {
                Lemma lemma = new Lemma();
                String k = (String) entry.getKey();
                Integer v = (Integer) entry.getValue();
                System.out.println(k + " " + v);
                List<Lemma> lemmas = lemmaRepository.findByLemma(k);
                if (lemmas.size() == 0) {
                    lemma.setLemma(k);
                    lemma.setFrequency(1);
                    List<Site> sites = siteRepository.findByUrl(siteToIndex);
                    Site foundSite = sites.get(0);
                    lemma.setSiteId(foundSite.getId());
                } else {
                    lemma = lemmas.get(0);
                    lemma.setFrequency(lemma.getFrequency() + 1);
                }
                lemmaRepository.save(lemma);
                IndexS indexS = new IndexS();
                indexS.setRank((float) v);
                lemmas = lemmaRepository.findByLemma(k);
                lemma = lemmas.get(0);
                indexS.setLemmaId(lemma.getId());
                List<Page> pages = pageRepository.getByPath(siteAddress);
                System.out.println(siteAddress);
                Page curPage = pages.get(0);
                indexS.setPageId(curPage.getId());
                indexRepository.save(indexS);
            }
        }
        Elements refs = doc.select("a");
        for (Element line : refs) {
            String s = line.attr("href");
            String upperDomen = getDomenName(s);
            if (s.length() < 3) {
                continue;
            }
            if (!(s.indexOf('#') == -1)) {
                continue;
            }
            if (siteAddress.contains(s)) {
                continue;
            }
            if (s.charAt(0) == 'h') {
                if (!domenName.equals(upperDomen)) {
                    continue;
                }
            } else if (s.charAt(0) == '/') {
                String uD = getDomenName(siteAddress);
                int end = siteAddress.indexOf("//");
                uD = siteAddress.substring(0, end + 2).concat(uD);
                s = uD.concat(s);
            } else {
                continue;
            }
            if (s.length() > 2) {
                List <Page> pages = pageRepository.getByPath(s);
                if (pages.size() == 0) { //  && refstoFile.size() < 90
                    System.out.println(s);
                    SitesList sl = new SitesList();
                    sl.setCurrentSite(s);
                    SiteIndexingServiceImpl loader = new SiteIndexingServiceImpl(sl,pageRepository, siteRepository,lemmaRepository,indexRepository);
                    loader.page = new Page();
                    loader.page.setPath(s);
                    List <Site> curSites = siteRepository.findByUrl(siteToIndex);
                    Site curSite = curSites.get(0);
                    loader.page.setSiteId(curSite.getId());
                    loader.siteAddress = s;
                    addPage(s);
                    loader.fork();
                    loader.join();
                }
            }
        }
    }

    private void addPage(String siteAddress) {
        //refsList.add(siteAddress);
        String str = "    ";
        int slashCount = siteAddress.length() - siteAddress.replace("/", "").length();
        String sToFile = str.repeat(slashCount - 2).concat(siteAddress);
        //refstoFile.add(sToFile);
    }

    public static Document loadPage(String siteAddress) {
        try {
            sleep(150);
            return Jsoup.connect(siteAddress).get();
        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private String getDomenName(String siteAddress) {
        //System.out.println(siteAddress);
        if (siteAddress.length() < 3) {
            return "";
        }
        int start = siteAddress.indexOf("//") + 2;
        int end = siteAddress.indexOf("/", start);
        if (end == -1) {
            end = siteAddress.length();
        }
        return siteAddress.substring(start, end);
    }
}