package CafeFinder.recipeshare.exception;

import static CafeFinder.recipeshare.util.ErrorMessage.NOT_VERIFY_CODE;

public class YetVerifyEmailException extends RuntimeException {

    public YetVerifyEmailException() {
        super(NOT_VERIFY_CODE.getMessage());
    }

}
