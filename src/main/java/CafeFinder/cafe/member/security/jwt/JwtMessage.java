package CafeFinder.cafe.member.security.jwt;

import lombok.Getter;

@Getter
public enum JwtMessage {

    INVALID_JWT("유효하지 않은 JWT 토큰입니다."),
    EXPIRED_JWT("만료된 JWT 토큰입니다."),
    UNSUPPORTED_JWT("지원하지 않는 JWT 토큰입니다."),
    GENERATE_ACCESSTOKEN("액세스 토큰 만료됨, 새로운 액세스 토큰 발급 완료"),
    ILLEGAL_JWT("잘못된 JWT 토큰입니다.");

    private final String message;

    JwtMessage(String message) {
        this.message = message;
    }

}
