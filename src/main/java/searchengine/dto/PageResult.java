package searchengine.dto;

import lombok.Data;

@Data
public class PageResult implements Comparable<PageResult>{
    private String site;

    private String siteName;
    private String uri;
    private String title;
    private String snippet;
    private float relevance;

    public String getUri() {
        return uri;
    }

    @Override
    public int compareTo(PageResult o) {
        String relevance1 = Float.toString(relevance);
        String relevance2 = Float.toString(o.relevance);
        return relevance1.compareTo(relevance2);
    }

    @Override
    public String toString() {
        return "{" + relevance + "}";
    }
}
