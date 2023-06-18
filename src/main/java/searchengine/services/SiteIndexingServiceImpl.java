package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import static java.lang.Thread.sleep;

@Service
@RequiredArgsConstructor
public class SiteIndexingServiceImpl extends RecursiveAction implements SiteIndexingService {
    static List<String> refsList = new ArrayList<>();
    public static List<String> refstoFile = new ArrayList<>();

    private final SitesList sites;
    String siteAddress;
    static String siteToIndex;

        /*public SiteIndexing (String siteAddress) {
        this.siteAddress = siteAddress;
    }*/

    public void StartIndexing(){
        ForkJoinPool pool = new ForkJoinPool();
        //SitesList sites = new SitesList();
        //System.out.println(sites.getSites().size());
        for (Site i : sites.getSites()) {
            SiteIndexingServiceImpl index = new SiteIndexingServiceImpl(sites);
            index.siteAddress = i.getUrl();
            index.siteToIndex = i.getUrl();
            index.compute();
            index.refstoFile.add(i.getUrl());
            pool.invoke(index);
        }
    }
    @Override
    public String getIndex(){
        StartIndexing();
        //System.out.println("ok");
        return "ok";
    };
    @Override
    public void compute() {
        String domenName = getDomenName(siteAddress);
        //System.out.println(siteToIndex);
        String givenDomen = getDomenName(siteToIndex);
        if (!(domenName.equals(givenDomen))){return;}
        Document doc = loadPage(siteAddress);
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
                if (!refsList.contains(s) && refstoFile.size() < 300) {
                    addPage(s);
                    SitesList sl = new SitesList();
                    sl.setCurrentSite(s);
                    SiteIndexingServiceImpl loader = new SiteIndexingServiceImpl(sl);
                    loader.siteAddress = s;

                    loader.fork();
                    loader.join();
                }
            }
        }
    }

    private void addPage(String siteAddress) {
        refsList.add(siteAddress);
        String str = "    ";
        int slashCount = siteAddress.length() - siteAddress.replace("/", "").length();
        String sToFile = str.repeat(slashCount - 2).concat(siteAddress);
        refstoFile.add(sToFile);
        System.out.println(sToFile);
    }

    private Document loadPage(String siteAddress) {
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