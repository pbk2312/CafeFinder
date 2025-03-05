package CafeFinder.cafe.util;

import CafeFinder.cafe.domain.CafeDistrict;
import CafeFinder.cafe.domain.CafeInfo;
import CafeFinder.cafe.domain.CafeTheme;
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
        CafeDistrict district = getDistrictByCode(cafeCode.substring(0, 2).toUpperCase());

        if (district == null) {
            log.debug("유효하지 않은 구 코드 (카페 ID: {}): {}", cafeCode, cafeCode.substring(0, 2));
            return Optional.empty();
        }

        return Optional.of(CafeInfo.create(
                cafeCode, name, address, district, hours, phone, imageUrl, review, themes
        ));
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
