package CafeFinder.cafe.service.impl;

import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.dto.CafeDto;
import CafeFinder.cafe.infrastructure.kafka.RecommendationRedisService;
import CafeFinder.cafe.service.interfaces.CafeService;
import CafeFinder.cafe.service.interfaces.MemberService;
import CafeFinder.cafe.service.interfaces.RecommendationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Log4j2
@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRedisService recommendationRedisService;
    private final CafeService cafeService;
    private final MemberService memberService;


    @Override
    public List<CafeDto> getRecommendationCafes(String accessToken) {

        log.info("사용자 추천 카페 로직 시작...");

        Member member = memberService.getMemberByToken(accessToken);

        String themeDistrict = recommendationRedisService.getMostClickedThemeDistrict(String.valueOf(member.getId()));

        if (themeDistrict == null) {
            return List.of();
        }

        String[] split = themeDistrict.split(":");
        String district = split[0];
        String theme = split[1];

        log.info("사용자 추천 테마 : {} , 행정구 : {} ", theme, district);
        return cafeService.getTopCafesByDistrictAndTheme(theme, district);

    }
}
