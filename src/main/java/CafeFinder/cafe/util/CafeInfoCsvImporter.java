package CafeFinder.cafe.util;

import CafeFinder.cafe.domain.CafeInfo;
import CafeFinder.cafe.service.cafe.CafeInfoService;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
        int skippedRows = 0; // 누락된 데이터 개수 카운트

        try (BufferedReader br = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true; // 첫 줄(헤더) 스킵
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split("\\|", -1); // 빈 값도 유지하도록 설정

                // 필수 필드 검증 (카페 ID, 카페명, 주소는 필수)
                if (data.length < 3 || data[0].trim().isEmpty() || data[1].trim().isEmpty() || data[2].trim()
                        .isEmpty()) {
                    log.warn("필수 데이터가 없는 행 스킵: {}", line);
                    skippedRows++;
                    continue;
                }

                CafeInfo cafe = createCafeInfo(data);
                cafeList.add(cafe);
            }

            // 저장할 데이터가 있을 때만 저장
            if (!cafeList.isEmpty()) {
                cafeInfoService.saveCafes(cafeList);
                log.info("CSV 데이터가 성공적으로 DB에 저장되었습니다! 저장된 행 수: {}", cafeList.size());
            } else {
                log.warn("저장할 데이터가 없습니다.");
            }

            log.info("스킵된 데이터 행 수: {} ", skippedRows);

        } catch (Exception e) {
            log.error("CSV 파일 읽기 오류: {}", e.getMessage(), e);
        }
    }

    private static CafeInfo createCafeInfo(String[] data) {
        CafeInfo cafe = CafeInfo.create(
                data[0].trim(),  // 카페 ID
                data[1].trim(),  // 카페명
                data[2].trim(),  // 주소
                data.length > 3 && !data[3].trim().isEmpty() ? data[3].trim() : null, // 영업시간 (없으면 null)
                data.length > 4 && !data[4].trim().isEmpty() ? data[4].trim() : null, // 전화번호 (없으면 null)
                data.length > 5 && !data[5].trim().isEmpty() ? data[5].trim() : null  // 이미지 URL (없으면 null)
        );
        return cafe;
    }

}
