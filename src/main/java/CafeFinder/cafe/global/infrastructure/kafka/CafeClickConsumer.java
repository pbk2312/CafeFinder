package CafeFinder.cafe.global.infrastructure.kafka;

import CafeFinder.cafe.cafe.domain.CafeTheme;
import CafeFinder.cafe.cafe.dto.CafeClickEventDto;
import CafeFinder.cafe.cafe.dto.CafeDto;
import CafeFinder.cafe.cafe.service.CafeService;
import CafeFinder.cafe.global.infrastructure.redis.RecommendationRedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CafeClickConsumer {

    private final KafkaKey kafkaKey;
    private final ObjectMapper objectMapper;
    private final CafeService cafeService;
    private final RecommendationRedisService redisService;

    private final Executor redisExecutor =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private KafkaStreams streams;

    @PostConstruct
    public void start() {
        streams = new KafkaStreams(buildTopology(), buildStreamsConfig());
        streams.start();
    }

    @PreDestroy
    public void stop() {
        if (streams != null) {
            streams.close();
        }
    }

    private Properties buildStreamsConfig() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaKey.getApplication().getId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaKey.getBootstrap().getServers());
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
            Serdes.StringSerde.class.getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
            Serdes.StringSerde.class.getName());
        return props;
    }

    private Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        builder
            .stream(kafkaKey.getTopic().getCafeClick(),
                Consumed.with(Serdes.String(), Serdes.String()))
            .flatMap(this::mapEventToKeyValues)
            .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
            .count(Materialized.with(Serdes.String(), Serdes.Long()))
            .toStream()
            .peek(this::recordClickCafe);

        return builder.build();
    }

    private Iterable<org.apache.kafka.streams.KeyValue<String, Long>> mapEventToKeyValues(
        String key, String jsonValue) {

        try {
            CafeClickEventDto event = objectMapper.readValue(jsonValue, CafeClickEventDto.class);
            CafeDto cafe = cafeService.getCafe(event.getCafeCode());

            return cafe.getThemes().stream()
                .map(theme -> org.apache.kafka.streams.KeyValue.pair(
                    makeCompositeKey(event.getMemberId(), theme, cafe.getDistrict().toString()),
                    1L))
                .toList();

        } catch (Exception e) {
            return List.of();
        }
    }

    private void recordClickCafe(String compositeKey, Long count) {
        redisExecutor.execute(() -> {
            try {
                redisService.recordClick(compositeKey, count);
            } catch (Exception e) {
                log.error("Reids에 클릭수 기록 실패: {} -> {}", compositeKey, count, e);
            }
        });
    }

    private String makeCompositeKey(String memberId, CafeTheme theme, String district) {
        return String.join(":", memberId, theme.name(), district);
    }
}
