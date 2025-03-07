package CafeFinder.cafe.jwt;

import lombok.Getter;

@Getter
public enum JwtErrorMessage {
    INVALID_JWT("유효하지 않은 JWT 토큰입니다."),
    EXPIRED_JWT("만료된 JWT 토큰입니다."),
    UNSUPPORTED_JWT("지원하지 않는 JWT 토큰입니다."),
    ILLEGAL_JWT("잘못된 JWT 토큰입니다.");

    private final String message;

    JwtErrorMessage(String message) {
        this.message = message;
    }

}
