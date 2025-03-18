package CafeFinder.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class MemberProfileDto {
    private String nickName;
    private String profileImagePath;
}
