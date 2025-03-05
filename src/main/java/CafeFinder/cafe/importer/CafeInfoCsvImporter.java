package CafeFinder.cafe.importer;

import CafeFinder.cafe.domain.CafeInfo;
import CafeFinder.cafe.service.cafe.CafeInfoService;
import CafeFinder.cafe.util.CsvParserUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
            br.readLine(); // 첫 줄(헤더) 건너뛰기
            String line;

            while ((line = br.readLine()) != null) {
                Optional<CafeInfo> cafe = CsvParserUtil.parseCafeInfo(line);
                if (cafe.isPresent()) {
                    cafeList.add(cafe.get());
                } else {
                    skippedRows++;  // 일반 변수 사용 가능
                }
            }

            saveCafes(cafeList);
            log.info("총 스킵된 데이터 행 수: {}", skippedRows);

        } catch (Exception e) {
            log.error("CSV 파일 읽기 오류: {}", e.getMessage(), e);
        }
    }


    private void saveCafes(List<CafeInfo> cafeList) {
        if (!cafeList.isEmpty()) {
            cafeInfoService.saveCafes(cafeList);
            log.info("CSV 데이터 저장 완료! 저장된 행 수: {}", cafeList.size());
        } else {
            log.warn("저장할 데이터가 없습니다.");
        }
    }

}
