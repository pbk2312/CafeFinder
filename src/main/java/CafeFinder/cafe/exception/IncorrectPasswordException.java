package CafeFinder.cafe.exception;

import static CafeFinder.cafe.util.ErrorMessage.PASSWORD_INCORRECT;

public class IncorrectPasswordException extends RuntimeException {

    public IncorrectPasswordException() {
        super(PASSWORD_INCORRECT.getMessage());
    }

}
