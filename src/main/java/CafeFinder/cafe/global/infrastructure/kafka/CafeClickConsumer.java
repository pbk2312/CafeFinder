package CafeFinder.cafe.global.infrastructure.kafka;

import CafeFinder.cafe.cafe.domain.CafeTheme;
import CafeFinder.cafe.cafe.dto.CafeClickEventDto;
import CafeFinder.cafe.cafe.dto.CafeDto;
import CafeFinder.cafe.cafe.service.CafeService;
import CafeFinder.cafe.global.infrastructure.redis.RecommendationRedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CafeClickConsumer {

    private final KafkaKey kafkaKey;
    private final ObjectMapper objectMapper;
    private final RecommendationRedisService redisService;
    private final CafeService cafeService;
    private KafkaStreams streams;

    @PostConstruct
    public void start() {
        streams = new KafkaStreams(buildStreamTopology(), getKafkaProperties());
        streams.start();
        log.info("Kafka Streams 애플리케이션이 시작되었습니다. 애플리케이션 ID: {}", kafkaKey.getApplication().getId());
    }

    private Properties getKafkaProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaKey.getApplication().getId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaKey.getBootstrap().getServers());
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        return props;
    }

    private org.apache.kafka.streams.Topology buildStreamTopology() {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> rawStream = builder.stream(
                kafkaKey.getTopic().getCafeClick(),
                Consumed.with(Serdes.String(), Serdes.String())
        );

        KStream<String, Long> aggregatedStream = processAndAggregateClicks(rawStream);
        KTable<String, Long> clickCounts = countClicks(aggregatedStream);
        persistCountsToRedisAsync(clickCounts);

        return builder.build();
    }

    private KStream<String, Long> processAndAggregateClicks(KStream<String, String> stream) {
        return stream.flatMap((key, value) -> {
            List<KeyValue<String, Long>> clickEvents = new ArrayList<>();
            try {
                CafeClickEventDto event = parseEvent(value);
                CafeDto cafe = cafeService.getCafe(event.getCafeCode());
                Set<CafeTheme> themes = cafe.getThemes();
                for (CafeTheme theme : themes) {
                    String redisKey = generateCompositeKey(event.getMemberId(), theme.toString(),
                            cafe.getDistrict().toString());
                    clickEvents.add(new KeyValue<>(redisKey, 1L));
                }

            } catch (JsonProcessingException e) {
                log.error("이벤트 파싱 실패 - 값: {}", value, e);
            } catch (Exception e) {
                log.error("클릭 이벤트 처리 실패 - 값: {}", value, e);
            }
            return clickEvents;
        });
    }

    private CafeClickEventDto parseEvent(String value) throws JsonProcessingException {
        return objectMapper.readValue(value, CafeClickEventDto.class);
    }

    private String generateCompositeKey(String memberId, String theme, String district) {
        return String.join(":", memberId, theme, district);
    }

    private KTable<String, Long> countClicks(KStream<String, Long> aggregatedStream) {
        return aggregatedStream
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
                .count(Materialized.with(Serdes.String(), Serdes.Long()));
    }

    private void persistCountsToRedisAsync(KTable<String, Long> counts) {
        counts.toStream().foreach((key, count) ->
                CompletableFuture.runAsync(() -> {
                    try {
                        redisService.updateMemberClickEvent(key, count);
                    } catch (Exception e) {
                        log.error("Redis 업데이트 실패 - key: {}, count: {}", key, count, e);
                    }
                })
        );
    }

    @PreDestroy
    public void stop() {
        if (streams != null) {
            streams.close();
            log.info("Kafka Streams 애플리케이션이 종료되었습니다.");
        }
    }

}
