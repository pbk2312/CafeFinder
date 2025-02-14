package CafeFinder.cafe.service.redis;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    /*
     * Refresh Token Redis 저장
     */
    public void saveRefreshToken(Long memberId, String refreshToken, int expiresIn) {
        String key = "refresh_token_" + memberId;
        redisTemplate.opsForValue().set(key, refreshToken, expiresIn, TimeUnit.SECONDS); // 7일 저장
        log.info("Refresh Token 저장: key={}, expiresIn={}초", key, expiresIn);
    }

    /*
     * Refresh Token Redis 삭제
     */
    public void deleteRefreshToken(Long memberId) {
        String key = "refresh_token_" + memberId;
        Boolean isDeleted = redisTemplate.delete(key);

        if (Boolean.TRUE.equals(isDeleted)) {
            log.info("Refresh Token 삭제 성공: key={}", key);
        } else {
            log.warn("Refresh Token 삭제 실패 또는 존재하지 않음: key={}", key);
        }
    }
    
}
