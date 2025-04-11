package CafeFinder.cafe.service.interfaces;

import CafeFinder.cafe.dto.AccessTokenDto;
import CafeFinder.cafe.dto.CafeDto;
import CafeFinder.cafe.dto.CafeScrapDto;
import CafeFinder.cafe.dto.ScrapCafeCodeDto;
import java.util.List;

public interface CafeScrapService {

    boolean cafeScraps(CafeScrapDto cafeScrapDto);

    List<CafeDto> getCafeScraps(AccessTokenDto accessTokenDto);

    List<ScrapCafeCodeDto> getCafeScrapCodes(AccessTokenDto accessTokenDto);


}
