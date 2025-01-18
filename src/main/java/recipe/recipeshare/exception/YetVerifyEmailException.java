package recipe.recipeshare.exception;

import static recipe.recipeshare.util.ErrorMessage.NOT_VERIFY_CODE;

public class YetVerifyEmailException extends RuntimeException {

    public YetVerifyEmailException() {
        super(NOT_VERIFY_CODE.getMessage());
    }

}
