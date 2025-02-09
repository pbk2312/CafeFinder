package recipe.recipeshare.util;

import lombok.Getter;

@Getter
public enum ViewMessage {

    SIGN_UP_SUCCESS("회원가입 성공"),
    LOGIN_SUCCESS("로그인 성공"),
    LOGOUT_SUCCESS("로그아웃 성공"),
    EMAIL_SEND_SUCCESS("인증번호 이메일 전송 성공"),
    EMAIL_VERIFY_SUCCESS("이메일 인증 성공");

    private final String message;

    ViewMessage(String message) {
        this.message = message;
    }

}
