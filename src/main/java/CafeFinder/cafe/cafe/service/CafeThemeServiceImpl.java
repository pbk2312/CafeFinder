package CafeFinder.cafe.cafe.service;

import CafeFinder.cafe.cafe.domain.CafeTheme;
import CafeFinder.cafe.cafe.dto.CafeThemeDto;
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
