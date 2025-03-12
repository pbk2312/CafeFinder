package CafeFinder.cafe.elasticSearch;

import CafeFinder.cafe.domain.CafeInfoDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CafeInfoSearchRepository extends ElasticsearchRepository<CafeInfoDocument, String> {

    // 구,테마 검색
    Page<CafeInfoDocument> findByDistrictAndThemesContainingOrderByReviewDesc(String district, String theme,
                                                                              Pageable pageable);

    // 카페명 또는 주소에 검색어가 포함된 경우 조회
    Page<CafeInfoDocument> findByNameContainingOrAddressContaining(String name, String address, Pageable pageable);

}
