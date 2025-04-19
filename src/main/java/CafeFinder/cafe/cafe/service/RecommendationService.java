package CafeFinder.cafe.cafe.service;

import CafeFinder.cafe.cafe.dto.CafeDto;
import java.util.List;

public interface RecommendationService {

    List<CafeDto> getRecommendationCafes();

    List<CafeDto> getGlobalRecommendationCafes();

}
