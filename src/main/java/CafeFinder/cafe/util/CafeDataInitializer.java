package CafeFinder.cafe.util;

import CafeFinder.cafe.service.cafe.CafeInfoService;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class CafeDataInitializer implements CommandLineRunner {

    private final CafeInfoService cafeInfoService;
    private final CafeInfoCsvImporter cafeInfoCsvImporter;

    @Override
    public void run(String... args) {
        if (cafeInfoService.countCafes() == 0) { // DB에 데이터가 없을 때만 실행
            String filePath = "/Users/park/cafeInfo/cafe_info.csv"; // CSV 경로
            if (Files.exists(Paths.get(filePath))) {
                cafeInfoCsvImporter.importCsv(filePath);
            } else {
                log.error("CSV 파일이 존재하지 않습니다: {}", filePath);
            }
        } else {
            log.info("DB에 이미 데이터가 존재하므로 CSV Import를 건너뜁니다.");
        }
    }

}
