package CafeFinder.cafe.infrastructure.kafka;

import CafeFinder.cafe.dto.CafeClickEventDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class CafeClickConsumer {

    @Value("${kafka.topic.cafe-click}")
    private String cafeClickTopicName;

    // application.properties에서 관리하는 redis 키 접두어
    @Value("${redis.key.prefix.recommendation}")
    private String redisKeyPrefix;

    private KafkaStreams streams;
    private final ObjectMapper objectMapper;
    private final RecommendationRedisService redisService;

    @Value("${kafka.bootstrap.servers:kafka:29092}")
    private String bootstrapServers;

    @Value("${kafka.application.id:cafe-click-aggregator}")
    private String applicationId;

    @PostConstruct
    @SuppressWarnings("resource")
    public void start() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> stream = createStreamFromTopic(builder);
        KStream<String, Long> aggregatedStream = aggregateMemberClicks(stream);
        KTable<String, Long> clickCounts = countAggregatedClicks(aggregatedStream);
        updateRedisCounts(clickCounts);

        streams = new KafkaStreams(builder.build(), props);
        streams.start();
        log.info("Kafka Streams 애플리케이션이 시작되었습니다. 애플리케이션 ID: {}", applicationId);
    }

    private void updateRedisCounts(KTable<String, Long> counts) {
        counts.toStream().foreach((compositeKey, count) -> {
            String redisKey = redisKeyPrefix + compositeKey;
            redisService.updateRecommendation(redisKey, count);
            log.info("Redis 업데이트 - 키: {}, 클릭 수: {}", redisKey, count);
        });
    }

    private KTable<String, Long> countAggregatedClicks(KStream<String, Long> aggregatedStream) {
        return aggregatedStream
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
                .count(Materialized.with(Serdes.String(), Serdes.Long()));
    }

    private KStream<String, Long> aggregateMemberClicks(KStream<String, String> stream) {
        return stream.flatMap((key, value) -> {
            List<KeyValue<String, Long>> result = new ArrayList<>();
            try {
                CafeClickEventDto event = parseClickEvent(value);

                event.getThemes().forEach(theme -> {
                    String compositeKey = event.getMemberId() + ":" + theme + ":" + event.getDistrict();
                    result.add(new KeyValue<>(compositeKey, 1L));
                });
            } catch (Exception e) {
                log.error("이벤트 파싱 오류 - 값: {}", value, e);
            }
            return result;
        });
    }

    private CafeClickEventDto parseClickEvent(String value) throws JsonProcessingException {
        return objectMapper.readValue(value, CafeClickEventDto.class);
    }

    private KStream<String, String> createStreamFromTopic(StreamsBuilder builder) {
        return builder.stream(cafeClickTopicName, Consumed.with(Serdes.String(), Serdes.String()));
    }

    @PreDestroy
    public void stop() {
        if (streams != null) {
            streams.close();
            log.info("Kafka Streams 애플리케이션이 종료되었습니다.");
        }
    }

}
