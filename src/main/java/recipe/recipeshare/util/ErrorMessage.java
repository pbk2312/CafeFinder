package recipe.recipeshare.util;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    MEMBER_NOT_FOUND("회원이 존재하지 않습니다."),
    PASSWORDS_DO_NOT_MATCH("두 비밀번호가 일치하지 않습니다."),
    PASSWORD_INCORRECT("비밀번호가 일치하지 않습니다."),
    SERVER_ERROR("서버 내부오류"),
    VALIDATION_FAILED("유효성 검증 실패");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
    
}
