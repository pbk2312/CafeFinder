package CafeFinder.cafe.cafe.service;

import CafeFinder.cafe.member.dto.AccessTokenDto;
import CafeFinder.cafe.cafe.dto.CafeDto;
import CafeFinder.cafe.cafe.dto.CafeScrapDto;
import CafeFinder.cafe.cafe.dto.ScrapCafeCodeDto;
import java.util.List;

public interface CafeScrapService {

    boolean cafeScraps(CafeScrapDto cafeScrapDto);

    List<CafeDto> getCafeScraps(AccessTokenDto accessTokenDto);

    List<ScrapCafeCodeDto> getCafeScrapCodes(AccessTokenDto accessTokenDto);


}
