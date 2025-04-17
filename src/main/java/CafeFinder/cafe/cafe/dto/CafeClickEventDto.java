package CafeFinder.cafe.cafe.dto;


import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CafeClickEventDto {

    private String memberId;
    private String cafeCode;
    private Set<String> themes;
    private String district;
    private long timestamp;   // 이벤트 발생 시간 (Epoch millis)

}
