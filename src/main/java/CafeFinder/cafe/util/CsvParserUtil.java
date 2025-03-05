package CafeFinder.cafe.util;

import CafeFinder.cafe.domain.CafeDistrict;
import CafeFinder.cafe.domain.CafeInfo;
import CafeFinder.cafe.domain.CafeReview;
import CafeFinder.cafe.domain.CafeTheme;
import CafeFinder.cafe.domain.GuReviewStats;
import CafeFinder.cafe.repository.CafeInfoRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CsvParserUtil {

    private static final String DELIMITER = "\\|";

    public static Optional<CafeInfo> parseCafeInfo(String line) {
        String[] data = line.split(DELIMITER, -1);

        if (!isValidData(data)) {
            log.debug("필수 데이터가 없는 행 스킵: {}", line);
            return Optional.empty();
        }

        String cafeCode = data[0].trim();
        String name = data[1].trim();
        String address = data[2].trim();
        String hours = getValueOrNull(data, 3);
        String phone = getValueOrNull(data, 4);
        String imageUrl = getValueOrNull(data, 5);
        Double review = parseReview(data, cafeCode);
        Set<CafeTheme> themes = parseThemes(data, cafeCode);

        // 3글자 코드 먼저 확인, 없으면 2글자 코드 사용
        CafeDistrict district = null;
        String upperCode = cafeCode.toUpperCase();

        if (upperCode.length() >= 3) {
            district = getDistrictByCode(upperCode.substring(0, 3));
        }
        if (district == null && upperCode.length() >= 2) {
            district = getDistrictByCode(upperCode.substring(0, 2));
        }

        if (district == null) {
            log.debug("유효하지 않은 구 코드 (카페 ID: {}): {}", cafeCode, cafeCode);
            return Optional.empty();
        }

        return Optional.of(CafeInfo.create(
                cafeCode, name, address, district, hours, phone, imageUrl, review, themes
        ));
    }


    public static GuReviewStats parse(String line) {
        String[] data = line.split(",", -1); // -1 옵션: 빈 문자열도 유지

        if (data.length < 3 || data[0].trim().isEmpty() || data[1].trim().isEmpty() || data[2].trim().isEmpty()) {
            log.warn("필수 데이터가 없는 행 스킵: {}", line);
            return null;
        }

        try {
            String guCode = data[0].trim();
            double averageRating = Double.parseDouble(data[1].trim());
            int totalReviews = Integer.parseInt(data[2].trim());

            return GuReviewStats.create(guCode, averageRating, totalReviews);
        } catch (NumberFormatException e) {
            log.warn("잘못된 숫자 형식: {}", line, e);
        } catch (Exception e) {
            log.error("데이터 파싱 중 오류 발생: {}", line, e);
        }
        return null;
    }


    public static Optional<CafeReview> parseCafeReview(String line, CafeInfoRepository cafeInfoRepository) {
        String[] parts = line.split(DELIMITER);
        if (parts.length < 3) {
            return Optional.empty();
        }

        String cafeCode = parts[0];
        String ratingStr = parts[1];
        String reviewText = parts[2];

        if (ratingStr.contains("평균 평점")) {
            return Optional.empty();
        }

        double rating;
        try {
            rating = Double.parseDouble(ratingStr);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        CafeInfo cafe = cafeInfoRepository.findById(cafeCode).orElse(null);
        if (cafe == null) {
            return Optional.empty();  // 카페 정보 없으면 스킵
        }

        return Optional.of(CafeReview.create(cafe, rating, reviewText));
    }


    private static boolean isValidData(String[] data) {
        return data.length >= 3 && !data[0].trim().isEmpty() && !data[1].trim().isEmpty() && !data[2].trim().isEmpty();
    }

    private static String getValueOrNull(String[] data, int index) {
        return (data.length > index && !data[index].trim().isEmpty()) ? data[index].trim() : null;
    }

    private static Double parseReview(String[] data, String cafeCode) {
        try {
            return (data.length > 6 && !data[6].trim().isEmpty()) ? Double.parseDouble(data[6].trim()) : 0.0;
        } catch (NumberFormatException e) {
            log.debug("잘못된 평점 값 (카페 ID: {}): {}, 기본값 0.0으로 설정", cafeCode, data[6]);
            return 0.0;
        }
    }

    private static Set<CafeTheme> parseThemes(String[] data, String cafeCode) {
        Set<CafeTheme> themes = new HashSet<>();
        if (data.length > 7 && !data[7].trim().isEmpty()) {
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

    private static CafeDistrict getDistrictByCode(String code) {
        return Arrays.stream(CafeDistrict.values())
                .filter(district -> district.name().equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }

}
