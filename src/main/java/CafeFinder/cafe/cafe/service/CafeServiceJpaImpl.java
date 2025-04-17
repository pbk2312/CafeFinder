package CafeFinder.cafe.cafe.service;

import CafeFinder.cafe.cafe.domain.Cafe;
import CafeFinder.cafe.cafe.domain.CafeTheme;
import CafeFinder.cafe.cafe.domain.SeoulDistrict;
import CafeFinder.cafe.cafe.dto.CafeDto;
import CafeFinder.cafe.cafe.dto.CafeMapDto;
import CafeFinder.cafe.cafe.dto.CafeReviewsResponseDto;
import CafeFinder.cafe.cafe.repository.CafeRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Profile("jpa")
@Service
@Slf4j
@RequiredArgsConstructor
public class CafeServiceJpaImpl implements CafeService {

    private final CafeRepository cafeRepository;

    @Override
    public CompletableFuture<CafeReviewsResponseDto> getCafeReviewsAsync(String cafeCode, int page) {
        return null;
    }

    @Override
    public void saveCafes(List<Cafe> cafes) {

    }

    @Override
    public long countCafes() {
        return 0;
    }

    @Override
    public Page<CafeDto> getCafesByDistrictAndTheme(String district, String theme, Pageable pageable) {
        SeoulDistrict seoulDistrict = SeoulDistrict.valueOf(district.toUpperCase());
        CafeTheme cafeTheme = CafeTheme.valueOf(theme.toUpperCase());
        Page<Cafe> cafes = cafeRepository.findByDistrictAndThemesContaining(seoulDistrict,
                cafeTheme, pageable);

        return cafes.map(cafe -> CafeDto.builder()
                .cafeCode(cafe.getCode())
                .name(cafe.getName())
                .address(cafe.getAddress())
                .district(cafe.getDistrict())
                .openingHours(cafe.getOpeningHours())
                .phoneNumber(cafe.getPhoneNumber())
                .imageUrl(cafe.getImageUrl())
                .averageRating(cafe.getAverageRating())
                .build());
    }

    @Override
    public Page<CafeDto> searchCafesByNameOrAddress(String keyword, Pageable pageable) {
        Page<Cafe> cafes = cafeRepository.findByNameContainingIgnoreCaseOrAddressContainingIgnoreCase(keyword, keyword,
                pageable);

        return cafes.map(CafeServiceJpaImpl::from);

    }


    @Override
    public CafeDto getCafe(String cafeCode) {
        return null;
    }

    @Override
    public List<CafeDto> getTopCafesByDistrictAndTheme(String district, String theme) {
        return List.of();
    }

    @Override
    public List<CafeDto> getMostClickedCafes(String district, String theme) {
        return List.of();
    }

    @Override
    public List<CafeMapDto> findCafesByDistance(double latitude, double longitude) {
        return List.of();
    }

    private static CafeDto from(Cafe cafe) {
        return CafeDto.builder()
                .cafeCode(cafe.getCode())
                .name(cafe.getName())
                .address(cafe.getAddress())
                .district(cafe.getDistrict())
                .openingHours(cafe.getOpeningHours())
                .phoneNumber(cafe.getPhoneNumber())
                .imageUrl(cafe.getImageUrl())
                .averageRating(cafe.getAverageRating())
                .themes(cafe.getThemes())
                .build();
    }

}
