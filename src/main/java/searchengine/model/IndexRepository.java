package searchengine.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<IndexS, Integer> {
    List<IndexS> findByLemmaId(Integer id);
    List<IndexS> findByPageId(Integer id);
}