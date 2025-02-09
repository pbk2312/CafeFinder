package CafeFinder.recipeshare.exception;

import static CafeFinder.recipeshare.util.ErrorMessage.PASSWORD_INCORRECT;

public class IncorrectPasswordException extends RuntimeException {

    public IncorrectPasswordException() {
        super(PASSWORD_INCORRECT.getMessage());
    }

}
