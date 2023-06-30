package searchengine.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//@Configuration
public interface SiteRepository extends CrudRepository<Site, Integer> {
    List<Site> findByUrl (String url);
}
