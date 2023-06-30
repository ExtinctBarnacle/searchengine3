package searchengine.model;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
//import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity(name = "page")
@NoArgsConstructor
@Component
public class Page  {

    /*@Override
    public <S extends Page> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Page> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }*/

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return id == page.id && siteId == page.siteId && code == page.code && path.equals(page.path) && content.equals(page.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, siteId, path, code, content);
    }

    //@NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    //@NotNull
    //@EmbeddedId
    @Column(name = "status_id")
    int siteId;

    //@NotNull
    @Column(name = "path",columnDefinition = "TEXT")
    String path;

    //@NotNull
    @Column(name = "code")
    int code;

    //@NotNull
    @Column(columnDefinition = "MEDIUMTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String content;
}
