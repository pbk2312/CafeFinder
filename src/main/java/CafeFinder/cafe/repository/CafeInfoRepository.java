package CafeFinder.cafe.repository;

import CafeFinder.cafe.domain.CafeInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CafeInfoRepository extends JpaRepository<CafeInfo, Long> {
}
