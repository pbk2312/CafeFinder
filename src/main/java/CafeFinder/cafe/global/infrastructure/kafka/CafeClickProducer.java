package CafeFinder.cafe.global.infrastructure.kafka;


import CafeFinder.cafe.cafe.dto.CafeClickEventDto;
import CafeFinder.cafe.member.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CafeClickProducer {

    private final KafkaKey kafkaKey;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCafeClickEvent(String cafeCode) {

        Long memberId = SecurityUtil.getMemberId();

        CafeClickEventDto eventDto = CafeClickEventDto.of(cafeCode, memberId);

        kafkaTemplate.send(kafkaKey.getTopic().getCafeClick(), cafeCode, eventDto);

    }


}

