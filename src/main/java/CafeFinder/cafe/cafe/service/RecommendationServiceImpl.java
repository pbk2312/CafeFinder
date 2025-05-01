package CafeFinder.cafe.cafe.service;

import CafeFinder.cafe.cafe.dto.CafeDto;
import CafeFinder.cafe.global.infrastructure.redis.RecommendationRedisService;
import CafeFinder.cafe.member.security.util.SecurityUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRedisService recommendationRedisService;
    private final CafeService cafeService;

    @Override
    public List<CafeDto> getRecommendationCafes() {

        Long memberId = SecurityUtil.getMemberId();

        String themeDistrict = recommendationRedisService.getThemeDistrictFavorited(
            String.valueOf(memberId));

        return getCafesByThemeDistrict(themeDistrict, true);
    }

    @Override
    public List<CafeDto> getGlobalRecommendationCafes() {
        String globalThemeDistrict = recommendationRedisService.getMostClickedGlobalClickedCafes();

        return getCafesByThemeDistrict(globalThemeDistrict, false);
    }

    private List<CafeDto> getCafesByThemeDistrict(String themeDistrict,
        boolean isMemberRecommendation) {
        if (isEmpty(themeDistrict)) {
            return List.of();
        }

        String[] split = themeDistrict.split(":");

        if (split.length < 2) {
            return List.of();
        }

        String district = split[0];
        String theme = split[1];

        if (isMemberRecommendation) {
            return cafeService.getTopCafesByDistrictAndTheme(theme, district);
        } else {
            return cafeService.getMostClickedCafes(theme, district);
        }
    }

    private static boolean isEmpty(String themeDistrict) {
        return themeDistrict == null;
    }

}
