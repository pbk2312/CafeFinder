package CafeFinder.cafe.cafe.repository;

import CafeFinder.cafe.cafe.domain.CafeScrap;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CafeScrapRepository extends JpaRepository<CafeScrap, Long> {

    List<CafeScrap> findAllByMemberId(Long memberId);
}
