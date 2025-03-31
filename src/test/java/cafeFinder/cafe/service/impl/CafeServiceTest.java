package cafeFinder.cafe.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import CafeFinder.cafe.dto.CafeDto;
import CafeFinder.cafe.infrastructure.elasticSearch.CafeSearchRepository;
import CafeFinder.cafe.infrastructure.elasticSearch.IndexedCafe;
import CafeFinder.cafe.service.impl.CafeServiceImpl;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.geo.Point;

@ExtendWith(MockitoExtension.class)
class CafeServiceTest {

    @Mock
    private CafeSearchRepository cafeSearchRepository;

    @InjectMocks
    private CafeServiceImpl cafeService;

    @Test
    void testFindCafesByDistance() {
        // given
        double latitude = 37.6763277;
        double longitude = 127.0452330;
        String distance = "3km";

        IndexedCafe indexedCafe = IndexedCafe.builder()
                .cafeCode("cafe1")
                .name("Test Cafe")
                .address("Test Address")
                .district("GN")
                .themes(List.of("COZY"))
                .averageRating(4.5)
                .reviewCount(10)
                .openingHours("09:00-18:00")
                .phoneNumber("010-1234-5678")
                .imageUrl("http://test-cafe.com/image.jpg")
                .location(new Point(latitude, longitude))
                .build();

        List<IndexedCafe> indexedCafes = List.of(indexedCafe);

        // when
        when(cafeSearchRepository.findCafesNearLocation(latitude, distance, longitude))
                .thenReturn(indexedCafes);

        // then
        List<CafeDto> result = cafeService.findCafesByDistance(latitude, longitude);
        verify(cafeSearchRepository).findCafesNearLocation(latitude, distance, longitude);
        assertNotNull(result);
        assertEquals(1, result.size());

        CafeDto cafeDto = result.get(0);
        assertEquals("cafe1", cafeDto.getCafeCode());
        assertEquals("Test Cafe", cafeDto.getName());

    }

}
