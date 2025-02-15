package CafeFinder.cafe.service.cafe;

import CafeFinder.cafe.domain.CafeInfo;
import CafeFinder.cafe.repository.CafeInfoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CafeInfoService {

    private final CafeInfoRepository cafeInfoRepository;

    @Transactional
    public void saveCafes(List<CafeInfo> cafes) {
        cafeInfoRepository.saveAll(cafes);
    }

    public long countCafes() {
        return cafeInfoRepository.count();  // DB에 저장된 카페 개수 확인
    }

    public List<CafeInfo> getAllCafes() {
        return cafeInfoRepository.findAll(); // 전체 카페 조회
    }


}
