
package searchengine.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
//@Configuration
public interface SiteRepository extends CrudRepository<Site, Integer> {
}
