package CafeFinder.cafe.elasticSearch;

import CafeFinder.cafe.domain.CafeInfoDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CafeInfoSearchRepository extends ElasticsearchRepository<CafeInfoDocument, String> {

    // 구,테마 검색
    Page<CafeInfoDocument> findByDistrictAndThemesContaining(String district, String theme, Pageable pageable);

    // 카페명 검색
    Page<CafeInfoDocument> findByNameContaining(String name, Pageable pageable);

}
