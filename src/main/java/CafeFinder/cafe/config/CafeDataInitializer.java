package CafeFinder.cafe.config;

import CafeFinder.cafe.importer.CafeInfoCsvImporter;
import CafeFinder.cafe.importer.GuReviewStatsCsvImporter;
import CafeFinder.cafe.service.cafe.CafeInfoService;
import CafeFinder.cafe.service.cafe.GuReviewStatsService;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class CafeDataInitializer {

    private final CafeInfoService cafeInfoService;
    private final GuReviewStatsService guReviewStatsService;
    private final CafeInfoCsvImporter cafeInfoCsvImporter;
    private final GuReviewStatsCsvImporter guReviewStatsCsvImporter;

    @Value("${file.cafeInfo.path}")
    private String cafeInfoPath;

    @Value("${file.guReviewStats.path}")
    private String guReviewStatsPath;

    @Bean
    public ApplicationRunner initializeCafeData() {
        return args -> {
            // 카페 정보 CSV import
            if (cafeInfoService.countCafes() == 0) { // DB에 데이터가 없을 때만 실행
                if (Files.exists(Paths.get(cafeInfoPath))) {
                    cafeInfoCsvImporter.importCsv(cafeInfoPath);
                    log.info("카페 정보 CSV import 완료");
                } else {
                    log.error("카페 정보 CSV 파일이 존재하지 않습니다: {}", cafeInfoPath);
                }
            } else {
                log.info("DB에 이미 카페 데이터가 존재하므로 CSV Import를 건너뜁니다.");
            }

            // 구별 리뷰 통계 CSV import
            if (guReviewStatsService.countGuReviewStats() == 0) { // DB에 데이터가 없을 때만 실행
                if (Files.exists(Paths.get(guReviewStatsPath))) {
                    guReviewStatsCsvImporter.importCsv(guReviewStatsPath);
                    log.info("구별 리뷰 통계 CSV import 완료");
                } else {
                    log.error("구별 리뷰 통계 CSV 파일이 존재하지 않습니다: {}", guReviewStatsPath);
                }
            } else {
                log.info("DB에 이미 구별 리뷰 통계 데이터가 존재하므로 CSV Import를 건너뜁니다.");
            }
        };
    }

}
