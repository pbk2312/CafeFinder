package CafeFinder.cafe.exception;

import static CafeFinder.cafe.util.ErrorMessage.PASSWORDS_DO_NOT_MATCH;

public class PasswordMismatchException extends RuntimeException {


    public PasswordMismatchException() {
        super(PASSWORDS_DO_NOT_MATCH.getMessage());
    }

}
