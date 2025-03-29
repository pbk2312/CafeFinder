package CafeFinder.cafe.importer;

import CafeFinder.cafe.repository.CafeRepository;
import CafeFinder.cafe.repository.CafeReviewRepository;
import CafeFinder.cafe.util.CsvParserUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CafeReviewImporter {

    private final CafeReviewRepository cafeReviewRepository;

    private final CafeRepository cafeInfoRepository;


    @Value("${file.cafe_reviews.path}")
    private String filePath;

    @Transactional
    public void saveReviewsFromCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                CsvParserUtil.parseCafeReview(line, cafeInfoRepository)
                        .ifPresent(cafeReviewRepository::save);
            }
        } catch (IOException e) {
            log.error("리뷰 CSV 파일을 읽는 중 오류 발생: {}", e.getMessage(), e);
        }
    }

}
