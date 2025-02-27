package CafeFinder.cafe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Builder
@Getter
public class UserUpdateDto {
    private String nickName;
    private MultipartFile newProfileImage;
}
