package CafeFinder.cafe.service.interfaces;

import CafeFinder.cafe.dto.CafeDto;
import java.util.List;

public interface RecommendationService {

    List<CafeDto> getRecommendationCafes(String accessToken);

}
