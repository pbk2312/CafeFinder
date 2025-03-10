package CafeFinder.cafe.exception;

import static CafeFinder.cafe.util.ErrorMessage.WRONG_CAFE_NAME;

public class WrongCafeNameException extends RuntimeException {
    public WrongCafeNameException() {
        super(WRONG_CAFE_NAME.getMessage());
    }
}
