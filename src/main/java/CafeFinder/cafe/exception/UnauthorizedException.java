package CafeFinder.cafe.exception;

import static CafeFinder.cafe.util.ErrorMessage.UNAUTHORIZED;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super(UNAUTHORIZED.getMessage());
    }
}
