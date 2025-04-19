package CafeFinder.cafe.member.security.config;

import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JwtKeyConfig {

    private final Jwtconfig jwtconfig;

    @Bean
    public SecretKey jwtSecretKey() {
        byte[] keyBytes = jwtconfig.getSecrets()
                .getAppkey()
                .getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
