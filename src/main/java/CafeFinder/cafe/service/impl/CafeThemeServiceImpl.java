package CafeFinder.cafe.service.impl;

import CafeFinder.cafe.domain.CafeTheme;
import CafeFinder.cafe.dto.CafeThemeDto;
import CafeFinder.cafe.service.interfaces.CafeThemeService;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CafeThemeServiceImpl implements CafeThemeService {

    @Override
    public List<CafeThemeDto> getCafeThemes() {
        return Arrays.stream(CafeTheme.values())
                .map(CafeThemeDto::fromEntity)
                .toList();
    }

}
