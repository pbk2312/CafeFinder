package CafeFinder.cafe.global.infrastructure.redis;

import CafeFinder.cafe.member.jwt.config.Jwtconfig;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_KEY_PREFIX = "blacklist_token:";
    private final Jwtconfig jwtconfig;


    public void addTokenToBlacklist(String refreshToken) {
        String key = BLACKLIST_KEY_PREFIX + refreshToken;
        redisTemplate.opsForValue()
                .set(key, refreshToken, jwtconfig.getValidation().getRefresh(), TimeUnit.MILLISECONDS);
        log.info("토큰을 블랙리스트에 추가했습니다: {}", refreshToken);
    }

    public boolean isTokenBlacklisted(String refreshToken) {
        String key = BLACKLIST_KEY_PREFIX + refreshToken;
        return redisTemplate.hasKey(key);
    }

}
