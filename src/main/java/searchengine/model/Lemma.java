package searchengine.model;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
//import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity(name = "lemma")
@NoArgsConstructor
@Component
public class Lemma {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lemma lemma1 = (Lemma) o;
        return id == lemma1.id && siteId == lemma1.siteId && frequency == lemma1.frequency && lemma.equals(lemma1.lemma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, siteId, lemma, frequency);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@NotNull
    @Column(name = "id")
    int id;
    //@NotNull
    //@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    int siteId;
    //@NotNull
    @Column(name = "lemma", columnDefinition = "VARCHAR(255)",nullable = false, unique = true)
    String lemma;
    @Column(name = "frequency")
    //@NotNull
    int frequency;
}

