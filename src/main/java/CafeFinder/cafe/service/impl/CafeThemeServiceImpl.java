package CafeFinder.cafe.service.impl;

import CafeFinder.cafe.domain.CafeTheme;
import CafeFinder.cafe.dto.CafeThemeDto;
import CafeFinder.cafe.service.interfaces.CafeThemeService;
import java.util.Arrays;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class CafeThemeServiceImpl implements CafeThemeService {

    @Override
    @Cacheable(value = "cafeThemes")
    public List<CafeThemeDto> getCafeThemes() {
        log.info("테마들 조회");
        return Arrays.stream(CafeTheme.values())
                .map(CafeThemeDto::fromEntity)
                .toList();
    }

}
