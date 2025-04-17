package CafeFinder.cafe.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Builder
@Getter
public class MemberUpdateDto {
    private String nickName;
    private MultipartFile newProfileImage;
}
