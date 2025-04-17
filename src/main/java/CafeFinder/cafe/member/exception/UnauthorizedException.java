package CafeFinder.cafe.member.exception;

import static CafeFinder.cafe.global.util.ErrorMessage.UNAUTHORIZED;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super(UNAUTHORIZED.getMessage());
    }
}
