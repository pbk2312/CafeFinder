package CafeFinder.cafe.config;

import CafeFinder.cafe.importer.CafeCsvImporter;
import CafeFinder.cafe.importer.CafeReviewImporter;
import CafeFinder.cafe.importer.SeoulDistrictStatusImporter;
import CafeFinder.cafe.repository.CafeReviewRepository;
import CafeFinder.cafe.service.interfaces.CafeService;
import CafeFinder.cafe.service.interfaces.SeoulDistrictStatusService;
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

    private final CafeService cafeInfoService;
    private final SeoulDistrictStatusService seoulDistrictStatusService;
    private final CafeCsvImporter cafeInfoCsvImporter;
    private final SeoulDistrictStatusImporter seoulDistrictStatusImporter;
    private final CafeReviewImporter cafeReviewImporter;
    private final CafeReviewRepository cafeReviewRepository;

    @Value("${file.cafeInfo.path}")
    private String cafeInfoPath;

    @Value("${file.guReviewStats.path}")
    private String guReviewStatsPath;

    @Value("${file.cafe_reviews.path}")
    private String cafeReviewsPath;

    @Bean
    public ApplicationRunner initializeCafeData() {
        return args -> {
            cafeInfoImport();

            guReviewStatsImport();

            reviewImport();

        };
    }

    private void reviewImport() {
        // 리뷰 데이터 CSV import
        if (cafeReviewRepository.count() == 0) { // 리뷰 데이터가 없을 때만 실행
            if (Files.exists(Paths.get(cafeReviewsPath))) {
                cafeReviewImporter.saveReviewsFromCSV();
                log.info("리뷰 데이터 CSV import 완료");
            } else {
                log.error("리뷰 CSV 파일이 존재하지 않습니다: {}", cafeReviewsPath);
            }
        } else {
            log.info("DB에 이미 리뷰 데이터가 존재하므로 CSV Import를 건너뜁니다.");
        }
    }

    private void guReviewStatsImport() {
        // 구별 리뷰 통계 CSV import
        if (seoulDistrictStatusService.countSeoulDistrict() == 0) { // DB에 데이터가 없을 때만 실행
            if (Files.exists(Paths.get(guReviewStatsPath))) {
                seoulDistrictStatusImporter.importCsv(guReviewStatsPath);
                log.info("구별 리뷰 통계 CSV import 완료");
            } else {
                log.error("구별 리뷰 통계 CSV 파일이 존재하지 않습니다: {}", guReviewStatsPath);
            }
        } else {
            log.info("DB에 이미 구별 리뷰 통계 데이터가 존재하므로 CSV Import를 건너뜁니다.");
        }
    }

    private void cafeInfoImport() {
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
    }

}
