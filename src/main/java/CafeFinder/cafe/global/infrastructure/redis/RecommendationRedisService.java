package CafeFinder.cafe.global.infrastructure.redis;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationRedisService {

    @Value("${redis.key.prefix.recommendation}")
    private String prefix;

    private final RedisTemplate<String, Object> redisTemplate;
    
    public void recordClick(String key, Long clickCount) {
        redisTemplate.opsForValue().increment(prefixed(key), clickCount);

        extractThemeAndDistrict(key).ifPresent(parts ->
            updateGlobalClickCount(parts[0], parts[1], clickCount)
        );
    }

    public String getMemberTopThemeDistrict(String memberId) {
        return findMaxClickKey(getMemberClickKeys(memberId))
            .flatMap(this::extractThemeAndDistrict)
            .map(parts -> parts[0] + ":" + parts[1])
            .orElse(null);
    }

    public String getMostClickedGlobalClickedCafes() {
        return findMaxClickKey(redisTemplate.keys(prefixed("global:*")))
            .flatMap(this::extractThemeAndDistrict)
            .map(parts -> parts[0] + ":" + parts[1])
            .orElse(null);
    }

    public void updateGlobalClickCount(String theme, String district, Long clickIncrement) {
        String key = prefixed("global:" + theme + ":" + district);
        redisTemplate.opsForValue().increment(key, clickIncrement);
    }

    private Set<String> getMemberClickKeys(String memberId) {
        return redisTemplate.keys(prefixed(memberId + ":*"));
    }

    private java.util.Optional<String> findMaxClickKey(Set<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return java.util.Optional.empty();
        }

        return keys.stream()
            .map(key -> new KeyClickCount(key, getClickCount(key)))
            .filter(k -> k.clickCount != null)
            .max(java.util.Comparator.comparingLong(k -> k.clickCount))
            .map(k -> k.key);
    }

    private Long getClickCount(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                log.warn("키 {}의 값 '{}'을 숫자로 변환하지 못했습니다.", key, value);
            }
        } else if (value != null) {
            log.warn("예상치 못한 타입입니다. 키 {}의 타입: {}", key, value.getClass());
        }
        return null;
    }


    private java.util.Optional<String[]> extractThemeAndDistrict(String redisKey) {
        if (redisKey == null) {
            return java.util.Optional.empty();
        }

        String[] parts = redisKey.split(":");
        return (parts.length == 4)
            ? java.util.Optional.of(new String[]{parts[2], parts[3]})
            : java.util.Optional.empty();
    }

    private String prefixed(String key) {
        return prefix + key;
    }

    private record KeyClickCount(String key, Long clickCount) {

    }
}
