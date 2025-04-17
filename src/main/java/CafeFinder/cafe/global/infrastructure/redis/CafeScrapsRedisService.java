package CafeFinder.cafe.global.infrastructure.redis;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CafeScrapsRedisService {

    private static final String SCRAPS_KEY_PATTERN = "scraps:%d_%s";

    private final RedisTemplate<String, String> redisTemplate;

    private String generateKey(Long memberId, String cafeCode) {
        return String.format(SCRAPS_KEY_PATTERN, memberId, cafeCode);
    }

    public boolean toggleCafeScrap(Long memberId, String cafeCode) {
        String key = generateKey(memberId, cafeCode);
        boolean isCurrentlyScrapped = isCafeScrapped(memberId, cafeCode);

        if (isCurrentlyScrapped) {
            redisTemplate.delete(key);
        } else {
            redisTemplate.opsForValue().set(key, String.valueOf(true));
        }
        return !isCurrentlyScrapped;
    }

    public boolean isCafeScrapped(Long memberId, String cafeCode) {
        String key = generateKey(memberId, cafeCode);
        String value = redisTemplate.opsForValue().get(key);
        return Boolean.parseBoolean(value);
    }

    public List<String> getCafeCodesForMember(Long memberId) {
        String pattern = String.format("scraps:%d_*", memberId);
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys.isEmpty()) {
            return Collections.emptyList();
        }

        return keys.stream()
                .map(key -> key.substring(key.indexOf('_') + 1))
                .toList();
    }

}
