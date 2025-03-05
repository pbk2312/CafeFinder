package CafeFinder.cafe.service.redis;


import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class RedisEmailVerifyService {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveVerificationCode(String email, String verificationCode) {
        redisTemplate.opsForValue().set(email, verificationCode, 5, TimeUnit.MINUTES);
    }

    public String getVerificationCode(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    public void changeStatus(String email) {
        redisTemplate.opsForValue().set("verified:" + email, "true", 30, TimeUnit.MINUTES);
    }

    public void deleteVerificationCode(String email) {
        String key = "verified:" + email;
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
            log.info("Redis 인증 데이터 삭제 완료: 이메일={}", email);
        } else {
            log.warn("Redis 인증 데이터 없음: 이메일={}", email);
        }
    }

}
