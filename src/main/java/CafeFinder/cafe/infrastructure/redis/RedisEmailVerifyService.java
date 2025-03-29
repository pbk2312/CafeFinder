package CafeFinder.cafe.infrastructure.redis;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisEmailVerifyService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String VERIFIED_PREFIX = "verified:";
    private static final long VERIFICATION_CODE_EXPIRATION_MINUTES = 5;
    private static final long VERIFIED_STATUS_EXPIRATION_MINUTES = 30;

    public void saveVerificationCode(String email, String verificationCode) {
        redisTemplate.opsForValue().set(
                email,
                verificationCode,
                VERIFICATION_CODE_EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );
    }

    public String getVerificationCode(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    public void changeStatus(String email) {
        redisTemplate.opsForValue().set(
                VERIFIED_PREFIX + email,
                "true",
                VERIFIED_STATUS_EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );
    }

    public void deleteVerificationCode(String email) {
        String key = VERIFIED_PREFIX + email;
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
            log.info("Redis 인증 데이터 삭제 완료: 이메일={}", email);
        } else {
            log.warn("Redis 인증 데이터 없음: 이메일={}", email);
        }
    }

}
