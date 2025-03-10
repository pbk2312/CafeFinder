package CafeFinder.cafe.exception;

import static CafeFinder.cafe.util.ErrorMessage.WRONG_SEARCH;

public class WrongSearchException extends RuntimeException {
    public WrongSearchException() {
        super(WRONG_SEARCH.getMessage());
    }
}
