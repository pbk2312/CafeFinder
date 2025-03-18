package CafeFinder.cafe.infrastructure.kafka;


import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.dto.CafeClickEventDto;
import CafeFinder.cafe.dto.CafeDto;
import CafeFinder.cafe.service.interfaces.CafeService;
import CafeFinder.cafe.service.interfaces.MemberService;
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
    private final MemberService memberService;
    private final CafeService cafeService;

    public void sendCafeClickEvent(String accessToken, String cafeCode) {

        if (isAccessTokenEmpty(accessToken)) {
            return;
        }

        Member member = memberService.getMemberByToken(accessToken);
        CafeDto cafeDto = cafeService.getCafe(cafeCode);

        String districtName = cafeDto.getDistrict().name();

        Set<String> themeNames = cafeDto.getThemes().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        CafeClickEventDto event = buildCafeClickEventDto(cafeCode, themeNames, districtName, member);

        kafkaTemplate.send(TOPIC, event.getCafeCode(), event);
    }

    private static boolean isAccessTokenEmpty(String accessToken) {
        return accessToken == null || accessToken.isEmpty();
    }

    private static CafeClickEventDto buildCafeClickEventDto(String cafeCode, Set<String> cafeThemes, String district,
                                                            Member member) {
        return CafeClickEventDto.builder()
                .memberId(String.valueOf(member.getId()))
                .cafeCode(cafeCode)
                .themes(cafeThemes)
                .district(district)
                .timestamp(System.currentTimeMillis())
                .build();
    }

}

