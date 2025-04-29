package CafeFinder.cafe.global.infrastructure.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "custom.kafka")
@RequiredArgsConstructor
public class KafkaKey {

    private final Topic topic;
    private final Bootstrap bootstrap;
    private final Application application;

    @Getter
    @RequiredArgsConstructor
    public static class Topic {
        private final String cafeClick;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Bootstrap {
        private final String servers;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Application {
        private final String id;
    }

}
