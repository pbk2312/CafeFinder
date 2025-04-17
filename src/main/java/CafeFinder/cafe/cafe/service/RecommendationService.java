package CafeFinder.cafe.cafe.service;

import CafeFinder.cafe.member.dto.AccessTokenDto;
import CafeFinder.cafe.cafe.dto.CafeDto;
import java.util.List;

public interface RecommendationService {

    List<CafeDto> getRecommendationCafes(AccessTokenDto accessTokenDto);

    List<CafeDto> getGlobalRecommendationCafes();

}
