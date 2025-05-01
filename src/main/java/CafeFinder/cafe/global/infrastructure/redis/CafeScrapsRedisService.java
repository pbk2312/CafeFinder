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
    private static final String SCRAPS_KEY_PATTERN_MEMBER = "scraps:%d_*";

    private final RedisTemplate<String, String> redisTemplate;

    // Cafe 토글
    public boolean toggleCafeScrap(Long memberId, String cafeCode) {
        String toggleCafeScarapKey = generateKey(memberId, cafeCode);
        boolean isCurrentlyScrapped = isCafeScrapped(toggleCafeScarapKey);

        if (isCurrentlyScrapped) {
            redisTemplate.delete(toggleCafeScarapKey);
        } else {
            redisTemplate.opsForValue().set(toggleCafeScarapKey, String.valueOf(true));
        }
        return !isCurrentlyScrapped;
    }

    public boolean isCafeScrappedKey(Long memberId, String cafeCode) {
        String cafeKey = generateKey(memberId, cafeCode);
        return isCafeScrapped(cafeKey);
    }

    private String generateKey(Long memberId, String cafeCode) {
        return String.format(SCRAPS_KEY_PATTERN, memberId, cafeCode);
    }

    public boolean isCafeScrapped(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return Boolean.parseBoolean(value);
    }

    public boolean removeCafeScrap(Long memberId, String cafeCode) {
        String key = generateKey(memberId, cafeCode);
        return redisTemplate.delete(key);
    }

    // 토글한 카페 목록 가져오기
    public List<String> getCafeCodesToggled(Long memberId) {
        String pattern = String.format(SCRAPS_KEY_PATTERN_MEMBER, memberId);

        Set<String> keys = redisTemplate.keys(pattern);

        if (keys.isEmpty()) {
            return Collections.emptyList();
        }

        return keys.stream()
            .map(key -> key.substring(key.indexOf('_') + 1))
            .toList();
    }
}
