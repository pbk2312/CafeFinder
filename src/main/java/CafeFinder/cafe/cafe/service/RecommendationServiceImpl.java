package CafeFinder.cafe.cafe.service;

import CafeFinder.cafe.member.domain.Member;
import CafeFinder.cafe.member.dto.AccessTokenDto;
import CafeFinder.cafe.cafe.dto.CafeDto;
import CafeFinder.cafe.global.infrastructure.redis.RecommendationRedisService;
import CafeFinder.cafe.member.service.MemberService;
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
    public List<CafeDto> getRecommendationCafes(AccessTokenDto accessTokenDto) {

        Member member = memberService.getMemberByToken(accessTokenDto.getAccessToken());
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
