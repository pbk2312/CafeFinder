package CafeFinder.cafe.infrastructure.kafka;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class RecommendationRedisService {

    @Value("${redis.key.prefix.recommendation}")
    private String redisKeyPrefix;

    private final RedisTemplate<String, Object> redisTemplate;

    public void updateRecommendation(String key, Long clickCount) {
        log.info("클릭 ... Redis에 업데이트 시작...");
        Long newCount = redisTemplate.opsForValue().increment(key, clickCount);
        log.info("Redis key = {} , new click count = {}", key, newCount);

        // 전역 클릭 수도 함께 업데이트
        String themeDistrict = extractThemeAndDistrict(key);
        if (themeDistrict != null) {
            String[] parts = themeDistrict.split(":");
            updateGlobalRecommendation(parts[0], parts[1], clickCount);
        }
    }

    // 사용자 클릭 기록에서 가장 많이 클릭한 테마와 지역을 가져오는 메서드
    public String getMostClickedThemeDistrict(String memberId) {
        Set<String> keys = getMemberClickKeys(memberId);
        String maxKey = findMaxClickKey(keys);
        return extractThemeAndDistrict(maxKey);
    }

    // Cold Start 대응: 테마와 지역구별 전역 클릭수 업데이트
    public void updateGlobalRecommendation(String theme, String district, Long clickIncrement) {
        String key = redisKeyPrefix + "global:" + theme + ":" + district;
        Long newCount = redisTemplate.opsForValue().increment(key, clickIncrement);
        log.info("Global key = {} updated with new count = {}", key, newCount);
    }

    // 전역 클릭수 중 가장 많이 클릭된 테마와 지역 구하기
    public String getMostGlobalClickedThemeDistrict() {
        String keyPattern = redisKeyPrefix + "global:*";
        Set<String> keys = redisTemplate.keys(keyPattern);
        String maxKey = findMaxClickKey(keys);
        return extractThemeAndDistrict(maxKey);
    }

    private Set<String> getMemberClickKeys(String memberId) {
        String keyPattern = redisKeyPrefix + memberId + ":*";
        return redisTemplate.keys(keyPattern);
    }

    private String findMaxClickKey(Set<String> keys) {
        String maxKey = null;
        long maxCount = 0;

        log.info("keys = {}", keys);
        if (keys != null) {
            for (String key : keys) {
                Object value = redisTemplate.opsForValue().get(key);
                Long count = null;

                if (value instanceof Number) {
                    count = ((Number) value).longValue();
                } else if (value instanceof String) {
                    try {
                        count = Long.parseLong((String) value);
                    } catch (NumberFormatException e) {
                        log.warn("키 {}의 값 {}을 숫자로 변환하는데 실패했습니다.", key, value);
                    }
                } else if (value != null) {
                    log.warn("예상치 못한 타입입니다. 키 {}의 타입: {}", key, value.getClass());
                }

                log.info("키: {} -> 클릭 수: {}", key, count);

                if (count != null && count > maxCount) {
                    maxCount = count;
                    maxKey = key;
                }
            }
        }
        return maxKey;
    }

    private String extractThemeAndDistrict(String redisKey) {
        if (redisKey == null) {
            return null;
        }

        String[] parts = redisKey.split(":");
        // recommendation:{memberId}:<theme>:<district>
        // recommendation:global:<theme>:<district>
        if (parts.length == 4) {
            return parts[2] + ":" + parts[3];
        }
        return null;
    }

}
