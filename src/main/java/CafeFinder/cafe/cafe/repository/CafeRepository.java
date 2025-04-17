package CafeFinder.cafe.cafe.repository;

import CafeFinder.cafe.cafe.domain.Cafe;
import CafeFinder.cafe.cafe.domain.CafeTheme;
import CafeFinder.cafe.cafe.domain.SeoulDistrict;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CafeRepository extends JpaRepository<Cafe, String> {

    @Query("SELECT DISTINCT c FROM Cafe c LEFT JOIN FETCH c.themes WHERE c.code = :code")
    Optional<Cafe> findCafeByCode(String code);

    Page<Cafe> findByDistrictAndThemesContaining(SeoulDistrict district, CafeTheme theme, Pageable pageable);

    Page<Cafe> findByNameContainingIgnoreCaseOrAddressContainingIgnoreCase(String nameKeyword, String addressKeyword,
                                                                           Pageable pageable);

}
