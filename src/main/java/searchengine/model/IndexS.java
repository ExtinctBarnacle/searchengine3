package searchengine.model;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import javax.persistence.*;
//import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity (name = "index_s")
@NoArgsConstructor
@Component
public class IndexS {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexS indexS = (IndexS) o;
        return id == indexS.id && pageId == indexS.pageId && lemmaId == indexS.lemmaId && Float.compare(indexS.rankS, rankS) == 0;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, pageId, lemmaId, rankS);
    }
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    int id;
    @Column(name = "page_id")
    int pageId;
    @Column(name = "lemma_id")
    int lemmaId;
    float rankS;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getPageId() {
        return pageId;
    }
    public void setPageId(int pageId) {
        this.pageId = pageId;
    }
    public int getLemmaId() {
        return lemmaId;
    }
    public void setLemmaId(int lemmaId) {
        this.lemmaId = lemmaId;
    }
    public float getRank() {
        return rankS;
    }
    public void setRank(float rankS) {
        this.rankS = rankS;
    }
}
