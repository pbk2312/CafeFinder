package CafeFinder.cafe.cafe.service.importer;

import CafeFinder.cafe.cafe.domain.SeoulDistrictStatus;
import CafeFinder.cafe.cafe.service.SeoulDistrictStatusService;
import CafeFinder.cafe.global.util.CsvParserUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeoulDistrictStatusImporter {

    private final SeoulDistrictStatusService guReviewStatsService;

    public void importCsv(String filePath) {
        List<SeoulDistrictStatus> statsList = new ArrayList<>();
        int skippedRows = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                SeoulDistrictStatus stats = CsvParserUtil.parseSeoulDistrictStatus(line);
                if (stats != null) {
                    statsList.add(stats);
                } else {
                    skippedRows++;
                }
            }

            if (!statsList.isEmpty()) {
                guReviewStatsService.saveGuReviewStats(statsList);
                log.info("구별 리뷰 통계 CSV 데이터 저장 완료! 저장된 행 수: {}", statsList.size());
            } else {
                log.warn("저장할 데이터가 없습니다.");
            }
            log.info("스킵된 데이터 행 수: {}", skippedRows);
        } catch (Exception e) {
            log.error("CSV 파일 읽기 오류: {}", e.getMessage(), e);
        }
    }

}

