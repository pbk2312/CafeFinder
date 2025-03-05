package CafeFinder.cafe.importer;

import CafeFinder.cafe.domain.GuReviewStats;
import CafeFinder.cafe.service.cafe.GuReviewStatsService;
import CafeFinder.cafe.util.CsvParserUtil;
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
public class GuReviewStatsCsvImporter {

    private final GuReviewStatsService guReviewStatsService;

    public void importCsv(String filePath) {
        List<GuReviewStats> statsList = new ArrayList<>();
        int skippedRows = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                GuReviewStats stats = CsvParserUtil.parse(line);
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

