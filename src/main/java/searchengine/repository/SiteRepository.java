package searchengine.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Site;

import java.util.List;

@Repository
//@Configuration
public interface SiteRepository extends CrudRepository<Site, Integer> {
    List<Site> findByUrl (String url);
}
