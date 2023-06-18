package searchengine.model;

import lombok.NoArgsConstructor;

import javax.persistence.*;
//import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity(name = "site")
@NoArgsConstructor
public class Site {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(Date statusTime) {
        this.statusTime = statusTime;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Site site = (Site) o;
        return id == site.id && status == site.status && statusTime.equals(site.statusTime) && lastError.equals(site.lastError) && url.equals(site.url) && name.equals(site.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, statusTime, lastError, url, name);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@NotNull
   // NOT NULL AUTO_INCREMENT
    int id;
    @Column(columnDefinition = "ENUM('INDEXING', 'INDEXED')")
    //@NotNull
    Status status;

    @Column(name = "status_time")
    //@NotNull
    Date statusTime;
    @Column(name = "last_error")
    //@NotNull
    String  lastError;
    @Column(name = "url",columnDefinition = "VARCHAR(255)")

    //@NotNull
    String url;
    @Column(name = "name",columnDefinition = "VARCHAR(255)")
    //@NotNull
    String name;

}
