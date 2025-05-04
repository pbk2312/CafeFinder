package CafeFinder.cafe.global.infrastructure.kafka;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Slf4j
class KafkaKeyTest {

    @Autowired
    KafkaKey kafkaKey;

    @Test
    @DisplayName("키 주입 테스트")
    void test_1() throws Exception {
        String id = kafkaKey.getApplication().getId();
        String servers = kafkaKey.getBootstrap().getServers();
        String cafeClick = kafkaKey.getTopic().getCafeClick();

        log.info("id = {}", id);
        log.info("servers = {}", servers);
        log.info("cafeClick = {}", cafeClick);

    }

}
