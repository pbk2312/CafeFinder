package CafeFinder.cafe.global.infrastructure.redis;

import CafeFinder.cafe.member.security.config.Jwtconfig;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.dao.DataAccessException;
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
        String key = generateBlacklistKey(refreshToken);
        Duration duration = Duration.ofMillis(jwtconfig.getValidation().getRefresh());

        try {
            redisTemplate.opsForValue().set(key, "true", duration);
            log.info("토큰을 블랙리스트에 추가했습니다: {}", refreshToken);
        } catch (DataAccessException e) {
            log.error("Redis에 블랙리스트 토큰을 저장하는 중 오류 발생", e);
        }
    }

    public boolean isTokenBlacklisted(String refreshToken) {
        String key = generateBlacklistKey(refreshToken);
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (DataAccessException e) {
            log.error("Redis에서 블랙리스트 토큰을 조회하는 중 오류 발생", e);
            return false;
        }
    }

    private String generateBlacklistKey(String token) {
        String tokenHash = DigestUtils.sha256Hex(token);
        return BLACKLIST_KEY_PREFIX + tokenHash;
    }

}
