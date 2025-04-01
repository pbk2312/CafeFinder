package CafeFinder.cafe.service.interfaces;

import CafeFinder.cafe.dto.AccessTokenDto;
import CafeFinder.cafe.dto.CafeDto;
import java.util.List;

public interface RecommendationService {

    List<CafeDto> getRecommendationCafes(AccessTokenDto accessTokenDto);

    List<CafeDto> getGlobalRecommendationCafes();

}
