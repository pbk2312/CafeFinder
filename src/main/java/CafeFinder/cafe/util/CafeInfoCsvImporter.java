package CafeFinder.cafe.util;

import CafeFinder.cafe.domain.CafeDistrict;
import CafeFinder.cafe.domain.CafeInfo;
import CafeFinder.cafe.domain.CafeTheme;
import CafeFinder.cafe.service.cafe.CafeInfoService;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
                String[] data = line.split("\\|", -1);
                if (data.length < 3 || data[0].trim().isEmpty() || data[1].trim().isEmpty() || data[2].trim()
                        .isEmpty()) {
                    log.warn("필수 데이터가 없는 행 스킵: {}", line);
                    skippedRows++;
                    continue;
                }
                CafeInfo cafe = createCafeInfo(data);
                if (cafe != null) {
                    cafeList.add(cafe);
                } else {
                    skippedRows++;
                }
            }
            if (!cafeList.isEmpty()) {
                cafeInfoService.saveCafes(cafeList);
                log.info("CSV 데이터가 성공적으로 DB에 저장되었습니다! 저장된 행 수: {}", cafeList.size());
            } else {
                log.warn("저장할 데이터가 없습니다.");
            }
            log.info("스킵된 데이터 행 수: {}", skippedRows);
        } catch (Exception e) {
            log.error("CSV 파일 읽기 오류: {}", e.getMessage(), e);
        }
    }

    private static CafeInfo createCafeInfo(String[] data) {
        String cafeCode = data[0].trim();
        String name = data[1].trim();
        String address = data[2].trim();
        String hours = data.length > 3 && !data[3].trim().isEmpty() ? data[3].trim() : null;
        String phone = data.length > 4 && !data[4].trim().isEmpty() ? data[4].trim() : null;
        String imageUrl = data.length > 5 && !data[5].trim().isEmpty() ? data[5].trim() : null;
        Double review = null;
        if (data.length > 6 && !data[6].trim().isEmpty()) {
            try {
                review = Double.parseDouble(data[6].trim());
            } catch (NumberFormatException e) {
                log.warn("잘못된 평점 값: {} (카페 ID: {})", data[6], cafeCode);
                return null;
            }
        }
        Set<CafeTheme> themes = new HashSet<>();
        if (data.length > 7 && !data[7].trim().isEmpty()) {
            String[] themeStrings = data[7].split(",");
            for (String themeStr : themeStrings) {
                try {
                    themes.add(CafeTheme.valueOf(themeStr.trim()));
                } catch (IllegalArgumentException e) {
                    log.warn("잘못된 테마 값: {} (카페 ID: {})", themeStr, cafeCode);
                }
            }
        }
        if (themes.isEmpty()) {
            log.warn("테마 값이 없음 (카페 ID: {}, 카페명: {})", cafeCode, name);
        }

        String districtCode = cafeCode.substring(0, 2).toUpperCase();
        CafeDistrict district = getDistrictByCode(districtCode);
        if (district == null) {
            log.warn("유효하지 않은 구 코드: {} (카페 ID: {})", districtCode, cafeCode);
            return null;
        }
        return CafeInfo.builder()
                .cafeCode(cafeCode)
                .name(name)
                .address(address)
                .district(district)
                .hours(hours)
                .phone(phone)
                .imageUrl(imageUrl)
                .review(review)
                .theme(themes.isEmpty() ? null : themes.iterator().next()) // 하나의 테마만 저장 (DB 구조에 따라 변경 가능)
                .build();
    }

    private static CafeDistrict getDistrictByCode(String code) {
        for (CafeDistrict district : CafeDistrict.values()) {
            if (district.name().equalsIgnoreCase(code)) {
                return district;
            }
        }
        return null;
    }
}
