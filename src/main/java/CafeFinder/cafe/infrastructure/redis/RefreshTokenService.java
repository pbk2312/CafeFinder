package CafeFinder.cafe.infrastructure.redis;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_KEY_PREFIX = "blacklist_token:";

    @Value("${jwt.secret}")
    private String jwtSecretKey;


    public void addTokenToBlacklist(String refreshToken) {
        long expirationMillis = getTokenExpiration(refreshToken);
        if (expirationMillis <= 0) {
            log.warn("토큰이 이미 만료되었거나 유효하지 않습니다: {}", refreshToken);
            return;
        }
        String key = BLACKLIST_KEY_PREFIX + refreshToken;
        redisTemplate.opsForValue().set(key, refreshToken, expirationMillis, TimeUnit.MILLISECONDS);
        log.info("토큰을 블랙리스트에 추가했습니다: {}", refreshToken);
    }

    public boolean isTokenBlacklisted(String refreshToken) {
        String key = BLACKLIST_KEY_PREFIX + refreshToken;
        return redisTemplate.hasKey(key);
    }

    private long getTokenExpiration(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Date expiration = claims.getExpiration();
            long remainingMillis = expiration.getTime() - System.currentTimeMillis();
            return remainingMillis > 0 ? remainingMillis : 0;
        } catch (JwtException e) {
            log.error("토큰 파싱 중 예외 발생: {}", e.getMessage());
            return 0;
        }
    }


}
