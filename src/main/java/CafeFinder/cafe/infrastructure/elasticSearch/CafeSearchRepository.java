package CafeFinder.cafe.infrastructure.elasticSearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CafeSearchRepository extends ElasticsearchRepository<IndexedCafe, String> {

    Page<IndexedCafe> findByDistrictAndThemesContaining(String district, String theme,
                                                        Pageable pageable);

    Page<IndexedCafe> findByNameContainingOrAddressContaining(String name, String address, Pageable pageable);

}
