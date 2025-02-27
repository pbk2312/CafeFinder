package CafeFinder.cafe.dto;

import CafeFinder.cafe.domain.MemberRole;
import CafeFinder.cafe.domain.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ProfileDto {

    private String nickName;

    private String email;

    private Provider provider; // 구글, 카카오 , 네이버, 일반

    private MemberRole memberRole;

    private String profileImagePath; // 프로필 이미지 파일 경로

}
