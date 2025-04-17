package CafeFinder.cafe.member.exception;

import static CafeFinder.cafe.global.util.ErrorMessage.PASSWORD_INCORRECT;

public class IncorrectPasswordException extends RuntimeException {

    public IncorrectPasswordException() {
        super(PASSWORD_INCORRECT.getMessage());
    }

}
