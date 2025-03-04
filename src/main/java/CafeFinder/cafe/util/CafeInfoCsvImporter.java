package CafeFinder.cafe.util;

import CafeFinder.cafe.domain.CafeDistrict;
import CafeFinder.cafe.domain.CafeInfo;
import CafeFinder.cafe.domain.CafeTheme;
import CafeFinder.cafe.service.cafe.CafeInfoService;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class CafeInfoCsvImporter {

    private final CafeInfoService cafeInfoService;

    public void importCsv(String filePath) {
        List<CafeInfo> cafeList = new ArrayList<>();
        int skippedRows = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                Optional<CafeInfo> cafe = parseCafeInfo(line);
                if (cafe.isPresent()) {
                    cafeList.add(cafe.get());
                } else {
                    skippedRows++;
                }
            }

            saveCafes(cafeList);
            log.info("총 스킵된 데이터 행 수: {}", skippedRows);

        } catch (Exception e) {
            log.error("CSV 파일 읽기 오류: {}", e.getMessage(), e);
        }
    }

    private Optional<CafeInfo> parseCafeInfo(String line) {
        String[] data = line.split("\\|", -1);

        if (!isValidData(data)) {
            log.warn("필수 데이터가 없는 행 스킵: {}", line);
            return Optional.empty();
        }

        String cafeCode = data[0].trim();
        String name = data[1].trim();
        String address = data[2].trim();
        String hours = getValueOrNull(data, 3);
        String phone = getValueOrNull(data, 4);
        String imageUrl = getValueOrNull(data, 5);

        Double review = parseReview(data, cafeCode);
        if (review == null) {
            return Optional.empty();
        }

        Set<CafeTheme> themes = parseThemes(data, cafeCode);

        CafeDistrict district = getDistrictByCode(cafeCode.substring(0, 2).toUpperCase());
        if (district == null) {
            log.warn("유효하지 않은 구 코드: {} (카페 ID: {})", cafeCode.substring(0, 2), cafeCode);
            return Optional.empty();
        }

        return Optional.of(CafeInfo.builder()
                .cafeCode(cafeCode)
                .name(name)
                .address(address)
                .district(district)
                .hours(hours)
                .phone(phone)
                .imageUrl(imageUrl)
                .review(review)
                .themes(themes)
                .build());
    }

    private void saveCafes(List<CafeInfo> cafeList) {
        if (!cafeList.isEmpty()) {
            cafeInfoService.saveCafes(cafeList);
            log.info("CSV 데이터가 성공적으로 DB에 저장되었습니다! 저장된 행 수: {}", cafeList.size());
        } else {
            log.warn("저장할 데이터가 없습니다.");
        }
    }

    private boolean isValidData(String[] data) {
        return data.length >= 3 && !data[0].trim().isEmpty() && !data[1].trim().isEmpty() && !data[2].trim().isEmpty();
    }

    private String getValueOrNull(String[] data, int index) {
        return (data.length > index && !data[index].trim().isEmpty()) ? data[index].trim() : null;
    }

    private Double parseReview(String[] data, String cafeCode) {
        try {
            return (data.length > 6 && !data[6].trim().isEmpty()) ? Double.parseDouble(data[6].trim()) : null;
        } catch (NumberFormatException e) {
            log.warn("잘못된 평점 값: {} (카페 ID: {})", data[6], cafeCode);
            return null;
        }
    }

    private Set<CafeTheme> parseThemes(String[] data, String cafeCode) {
        Set<CafeTheme> themes = new HashSet<>();
        if (data.length > 7 && !data[7].trim().isEmpty()) {
            for (String themeStr : data[7].split(",")) {
                try {
                    themes.add(CafeTheme.valueOf(themeStr.trim()));
                } catch (IllegalArgumentException e) {
                    log.warn("잘못된 테마 값: {} (카페 ID: {})", themeStr, cafeCode);
                }
            }
        }
        return themes;
    }

    private CafeDistrict getDistrictByCode(String code) {
        return Arrays.stream(CafeDistrict.values())
                .filter(district -> district.name().equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }

}
