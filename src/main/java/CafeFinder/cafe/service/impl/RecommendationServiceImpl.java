package CafeFinder.cafe.service.impl;

import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.dto.CafeDto;
import CafeFinder.cafe.infrastructure.redis.RecommendationRedisService;
import CafeFinder.cafe.service.interfaces.CafeService;
import CafeFinder.cafe.service.interfaces.MemberService;
import CafeFinder.cafe.service.interfaces.RecommendationService;
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
    private final MemberService memberService;

    @Override
    public List<CafeDto> getRecommendationCafes(String accessToken) {
        log.info("사용자 추천 카페 로직 시작...");

        Member member = memberService.getMemberByToken(accessToken);
        String themeDistrict = recommendationRedisService.getMemberTopThemeDistrict(String.valueOf(member.getId()));

        return getCafesByThemeDistrict(themeDistrict, true);
    }

    @Override
    public List<CafeDto> getGlobalRecommendationCafes() {
        String globalThemeDistrict = recommendationRedisService.getMostClickedGlobalClickedCafes();

        return getCafesByThemeDistrict(globalThemeDistrict, false);
    }

    private List<CafeDto> getCafesByThemeDistrict(String themeDistrict, boolean isMemberRecommendation) {
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
