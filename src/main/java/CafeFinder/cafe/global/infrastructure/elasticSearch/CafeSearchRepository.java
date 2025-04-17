package CafeFinder.cafe.global.infrastructure.elasticSearch;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CafeSearchRepository extends ElasticsearchRepository<IndexedCafe, String> {

    Page<IndexedCafe> findByDistrictAndThemesContaining(String district, String theme,
                                                        Pageable pageable);

    Page<IndexedCafe> findByNameContainingOrAddressContaining(String name, String address, Pageable pageable);


    @Query("{\"bool\": {\"filter\": {\"geo_distance\": {\"distance\": \"?1\", \"location\": {\"lat\": ?0, \"lon\": ?2}}}}}")
    List<IndexedCafe> findCafesNearLocation(double latitude, String distance, double longitude);

    Optional<IndexedCafe> findByCafeCode(String cafeCode);

}
