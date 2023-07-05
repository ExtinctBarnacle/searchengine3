package searchengine.model;

import com.sun.istack.NotNull;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "page")
//@Table(name = "page", indexes = {@Index(name = "path", columnList = "path, site_id", unique = true)})
@NoArgsConstructor
@Component
public class Page  {

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    //@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "site_id", referencedColumnName = "id")
    @NotNull
    @Column(name = "site_id")
    //@Column(name = "site_id")
    int siteId;

    @Column(length = 1000, columnDefinition = "VARCHAR(515)", nullable = false)
    String path;

    @Column(name = "code")
    int code;

    @Column(length = 16777215, columnDefinition = "mediumtext")
    //@Column(columnDefinition = "MEDIUMTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    String content;
}