package CafeFinder.cafe.member.service;

import CafeFinder.cafe.cafe.dto.CafeDto;
import CafeFinder.cafe.cafe.dto.CafeScrapDto;
import CafeFinder.cafe.cafe.dto.ScrapCafeCodeDto;
import java.util.List;

public interface CafeScrapService {

    boolean cafeScraps(CafeScrapDto cafeScrapDto);

    List<CafeDto> getCafeScraps();

    List<ScrapCafeCodeDto> getCafeScrapCodes();


}
