package CafeFinder.cafe.cafe.exception;

import static CafeFinder.cafe.global.util.ErrorMessage.WRONG_SEARCH;

public class WrongSearchException extends RuntimeException {
    public WrongSearchException() {
        super(WRONG_SEARCH.getMessage());
    }
}
