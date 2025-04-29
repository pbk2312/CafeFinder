package CafeFinder.cafe.cafe.dto;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CafeClickEventDto {

    private String memberId;
    private String cafeCode;
    private long timestamp;

    public static CafeClickEventDto of(String cafeCode, Long memberId) {
        return CafeClickEventDto.builder()
                .memberId(String.valueOf(memberId))
                .cafeCode(cafeCode)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
}
