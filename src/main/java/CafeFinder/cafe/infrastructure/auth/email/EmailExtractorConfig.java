package CafeFinder.cafe.infrastructure.auth.email;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailExtractorConfig {

    @Bean
    public EmailExtractor kakaoEmailExtractor() {
        return new KakaoEmailExtractor();
    }

    @Bean
    public EmailExtractor naverEmailExtractor() {
        return new NaverEmailExtractor();
    }

    @Bean
    public EmailExtractor googleEmailExtractor() {
        return new GoogleEmailExtractor();
    }

    @Bean
    public CompositeEmailExtractor compositeEmailExtractor(List<EmailExtractor> extractors) {
        return new CompositeEmailExtractor(extractors);
    }

}
