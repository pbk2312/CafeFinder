package CafeFinder.cafe.cafe.service.importer;

import CafeFinder.cafe.cafe.domain.Cafe;
import CafeFinder.cafe.cafe.service.CafeService;
import CafeFinder.cafe.global.util.CsvParserUtil;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CafeCsvImporter {

    private final CafeService cafeInfoService;
    private final MeterRegistry meterRegistry;

    public void importCsv(String filePath) {
        Timer.Sample sample = Timer.start(meterRegistry); // 타이머 시작

        List<Cafe> cafeList = new ArrayList<>();
        int skippedRows = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            br.readLine(); // 헤더 건너뛰기
            String line;
            while ((line = br.readLine()) != null) {
                Optional<Cafe> cafe = CsvParserUtil.parseCafe(line);
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
        } finally {
            // 타이머 종료 후 메트릭 등록
            sample.stop(Timer.builder("csv.import.time")
                    .description("CSV 파일 import에 소요된 시간")
                    .register(meterRegistry));
        }
    }

    private void saveCafes(List<Cafe> cafeList) {
        if (!cafeList.isEmpty()) {
            cafeInfoService.saveCafes(cafeList);
            log.info("CSV 데이터 저장 완료! 저장된 행 수: {}", cafeList.size());
        } else {
            log.warn("저장할 데이터가 없습니다.");
        }
    }

}
