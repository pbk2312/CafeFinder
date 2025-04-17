package CafeFinder.cafe.global.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailVerifyDto {

    @NotBlank(message = "이메일은 필수 값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotBlank(message = "인증 번호는 필수 값입니다.")
    private String code;

}
