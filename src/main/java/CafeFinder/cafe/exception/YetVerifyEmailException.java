package CafeFinder.cafe.exception;

import static CafeFinder.cafe.util.ErrorMessage.NOT_VERIFY_CODE;

public class YetVerifyEmailException extends RuntimeException {

    public YetVerifyEmailException() {
        super(NOT_VERIFY_CODE.getMessage());
    }

}
