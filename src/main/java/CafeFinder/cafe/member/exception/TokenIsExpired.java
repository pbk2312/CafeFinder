package CafeFinder.cafe.member.exception;

import static CafeFinder.cafe.global.util.ErrorMessage.EXPRIED_TOKEN;

public class TokenIsExpired extends RuntimeException {
    public TokenIsExpired() {
        super(EXPRIED_TOKEN.getMessage());
    }
}
