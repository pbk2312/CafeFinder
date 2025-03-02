package CafeFinder.cafe.util;

import CafeFinder.cafe.domain.GuReviewStats;
import CafeFinder.cafe.service.cafe.GuReviewStatsService;
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

                String[] data = line.split(",", -1);
                if (data.length < 3 || data[0].trim().isEmpty() || data[1].trim().isEmpty() || data[2].trim()
                        .isEmpty()) {
                    log.warn("필수 데이터가 없는 행 스킵: {}", line);
                    skippedRows++;
                    continue;
                }

                GuReviewStats stats = createGuReviewStats(data);
                if (stats != null) {
                    statsList.add(stats);
                } else {
                    skippedRows++;
                }
            }

            if (!statsList.isEmpty()) {
                guReviewStatsService.saveGuReviewStats(statsList);
                log.info("구별 리뷰 통계 CSV 데이터가 성공적으로 DB에 저장되었습니다! 저장된 행 수: {}", statsList.size());
            } else {
                log.warn("저장할 데이터가 없습니다.");
            }
            log.info("스킵된 데이터 행 수: {}", skippedRows);
        } catch (Exception e) {
            log.error("CSV 파일 읽기 오류: {}", e.getMessage(), e);
        }
    }

    private static GuReviewStats createGuReviewStats(String[] data) {
        try {
            String guCode = data[0].trim();
            double averageRating = Double.parseDouble(data[1].trim());
            int totalReviews = Integer.parseInt(data[2].trim());

            return GuReviewStats.builder()
                    .guCode(guCode)
                    .averageRating(averageRating)
                    .totalReviews(totalReviews)
                    .build();
        } catch (NumberFormatException e) {
            log.warn("잘못된 숫자 형식: {}", data);
            return null;
        }
    }

}
