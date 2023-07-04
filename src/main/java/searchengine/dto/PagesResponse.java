package searchengine.dto;

import lombok.Data;

import java.util.List;

@Data
public class PagesResponse {
    boolean result;
    int count;
    private List<PageResult> data;
}
