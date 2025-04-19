package CafeFinder.cafe.global.infrastructure.kafka;


import CafeFinder.cafe.cafe.dto.CafeClickEventDto;
import CafeFinder.cafe.cafe.dto.CafeDto;
import CafeFinder.cafe.cafe.service.CafeService;
import CafeFinder.cafe.member.security.util.SecurityUtil;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CafeClickProducer {

    private static final String TOPIC = "cafe-click-topic";
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CafeService cafeService;

    public void sendCafeClickEvent(String cafeCode) {

        Long memberId = SecurityUtil.getMemberId();

        CafeDto cafeDto = cafeService.getCafe(cafeCode);

        String districtName = cafeDto.getDistrict().name();

        Set<String> themeNames = cafeDto.getThemes().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        CafeClickEventDto event = buildCafeClickEventDto(cafeCode, themeNames, districtName, memberId);

        kafkaTemplate.send(TOPIC, event.getCafeCode(), event);
    }

    private static boolean isAccessTokenEmpty(String accessToken) {
        return accessToken == null || accessToken.isEmpty();
    }

    private static CafeClickEventDto buildCafeClickEventDto(String cafeCode, Set<String> cafeThemes, String district,
                                                            Long memberId) {
        return CafeClickEventDto.builder()
                .memberId(String.valueOf(memberId))
                .cafeCode(cafeCode)
                .themes(cafeThemes)
                .district(district)
                .timestamp(System.currentTimeMillis())
                .build();
    }

}

