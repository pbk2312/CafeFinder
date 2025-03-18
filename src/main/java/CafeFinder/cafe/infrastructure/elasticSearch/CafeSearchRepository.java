package CafeFinder.cafe.infrastructure.elasticSearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CafeSearchRepository extends ElasticsearchRepository<IndexedCafe, String> {

    // 구,테마 검색
    Page<IndexedCafe> findByDistrictAndThemesContaining(String district, String theme,
                                                        Pageable pageable);

    // 카페명 또는 주소에 검색어가 포함된 경우 조회
    Page<IndexedCafe> findByNameContainingOrAddressContaining(String name, String address, Pageable pageable);

}
