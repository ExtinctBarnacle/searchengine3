/*
package searchengine.model;

import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
public class Index {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Index index = (Index) o;
        return id == index.id && pageId == index.pageId && lemmaId == index.lemmaId && Float.compare(index.rank, rank) == 0;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, pageId, lemmaId, rank);
    }
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    int id;
    @Column(name = "page_id")
    int pageId;
    @Column(name = "lemma_id")
    int lemmaId;
    float rank;

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
        return rank;
    }
    public void setRank(float rank) {
        this.rank = rank;
    }
}*/
