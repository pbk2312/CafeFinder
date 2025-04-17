package CafeFinder.cafe.global.util;

import CafeFinder.cafe.cafe.domain.Cafe;
import CafeFinder.cafe.cafe.domain.CafeReview;
import CafeFinder.cafe.cafe.domain.CafeTheme;
import CafeFinder.cafe.cafe.domain.SeoulDistrict;
import CafeFinder.cafe.cafe.domain.SeoulDistrictStatus;
import CafeFinder.cafe.cafe.repository.CafeRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class CsvParserUtil {

    private CsvParserUtil() {
        // 인스턴스화 방지
    }

    private static final String DELIMITER = "\\|";

    public static Optional<Cafe> parseCafe(String line) {
        if (line.startsWith("카페ID|")) {
            log.debug("헤더 행 스킵: {}", line);
            return Optional.empty();
        }

        String[] data = line.split(DELIMITER, -1);
        if (!hasRequiredCafeInfoData(data)) {
            log.debug("필수 데이터가 없는 행 스킵: {}", line);
            return Optional.empty();
        }

        String cafeCode = nullIfBlank(data[0]);
        String name = nullIfBlank(data[1]);
        String address = nullIfBlank(data[2]);
        String openingHours = nullIfBlank(data[3]);
        String phoneNumber = nullIfBlank(data[4]);
        String imageUrl = nullIfBlank(data[5]);
        Double averageRating = parseAverageRating(data, cafeCode);
        Set<CafeTheme> themes = parseThemes(data, cafeCode);
        Double latitude = parseDoubleOrNull(nullIfBlank(data[8]));
        Double longitude = parseDoubleOrNull(nullIfBlank(data[9]));

        SeoulDistrict district = resolveDistrictFromCafeCode(cafeCode);
        if (district == null) {
            log.debug("유효하지 않은 구 코드 (카페 ID: {}): {}", cafeCode, cafeCode);
            return Optional.empty();
        }

        return Optional.of(
                Cafe.create(cafeCode, name, address, district, openingHours, phoneNumber, imageUrl, averageRating,
                        themes, latitude, longitude)
        );
    }

    public static SeoulDistrictStatus parseSeoulDistrictStatus(String line) {
        String[] data = line.split(",", -1);
        if (data.length < 3 || data[0].trim().isEmpty() || data[1].trim().isEmpty() || data[2].trim().isEmpty()) {
            log.warn("필수 데이터가 없는 행 스킵: {}", line);
            return null;
        }
        try {
            String guCode = data[0].trim();
            double averageRating = Double.parseDouble(data[1].trim());
            int totalReviews = Integer.parseInt(data[2].trim());
            return SeoulDistrictStatus.create(guCode, averageRating, totalReviews);
        } catch (NumberFormatException e) {
            log.warn("잘못된 숫자 형식: {}", line, e);
        } catch (Exception e) {
            log.error("데이터 파싱 중 오류 발생: {}", line, e);
        }
        return null;
    }

    public static Optional<CafeReview> parseCafeReview(String line, CafeRepository cafeInfoRepository) {
        String[] parts = line.split(DELIMITER);
        if (parts.length < 3) {
            return Optional.empty();
        }

        String cafeCode = parts[0].trim();
        String ratingStr = parts[1].trim();
        String reviewText = parts[2].trim();

        if (ratingStr.contains("평균 평점")) {
            return Optional.empty();
        }

        double rating;
        try {
            rating = Double.parseDouble(ratingStr);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        return cafeInfoRepository.findById(cafeCode)
                .map(cafe -> CafeReview.create(cafe, rating, reviewText));
    }

    private static boolean hasRequiredCafeInfoData(String[] data) {
        return data.length >= 3 && !data[0].trim().isEmpty() && !data[1].trim().isEmpty() && !data[2].trim().isEmpty();
    }

    private static String nullIfBlank(String value) {
        if (value == null || value.trim().isEmpty() || value.trim().equalsIgnoreCase("NULL") || value.trim()
                .equalsIgnoreCase("NONE")) {
            return null;
        }
        return value.trim();
    }

    private static Double parseAverageRating(String[] data, String cafeCode) {
        if (data.length > 6 && !data[6].trim().isEmpty()) {
            try {
                return Double.parseDouble(data[6].trim());
            } catch (NumberFormatException e) {
                log.debug("잘못된 평점 값 (카페 ID: {}): {}. 기본값 0.0 적용", cafeCode, data[6]);
            }
        }
        return 0.0;
    }

    private static Double parseDoubleOrNull(String value) {
        try {
            return (value != null) ? Double.parseDouble(value) : null;
        } catch (NumberFormatException e) {
            log.debug("숫자 변환 실패: {}", value);
            return null;
        }
    }

    private static Set<CafeTheme> parseThemes(String[] data, String cafeCode) {
        Set<CafeTheme> themes = new HashSet<>();
        if (data.length > 7 && !data[7].trim().isEmpty() && !data[7].trim().equalsIgnoreCase("NULL") && !data[7].trim()
                .equalsIgnoreCase("NONE")) {
            for (String themeStr : data[7].split(",")) {
                try {
                    themes.add(CafeTheme.valueOf(themeStr.trim()));
                } catch (IllegalArgumentException e) {
                    log.debug("잘못된 테마 값 (카페 ID: {}): {}", cafeCode, themeStr);
                }
            }
        }
        return themes;
    }

    private static SeoulDistrict resolveDistrictFromCafeCode(String cafeCode) {
        String upperCode = cafeCode.toUpperCase();
        if (upperCode.length() >= 3) {
            SeoulDistrict district = getDistrictByCode(upperCode.substring(0, 3));
            if (district != null) {
                return district;
            }
        }
        if (upperCode.length() >= 2) {
            return getDistrictByCode(upperCode.substring(0, 2));
        }
        return null;
    }

    private static SeoulDistrict getDistrictByCode(String code) {
        return Arrays.stream(SeoulDistrict.values())
                .filter(district -> district.name().equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }

}
