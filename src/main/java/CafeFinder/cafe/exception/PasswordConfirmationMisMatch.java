package CafeFinder.cafe.exception;

import static CafeFinder.cafe.util.ErrorMessage.PASSWORD_CONFIRMATION_MISMATCH;

public class PasswordConfirmationMisMatch extends RuntimeException {


    public PasswordConfirmationMisMatch() {
        super(PASSWORD_CONFIRMATION_MISMATCH.getMessage());
    }

}
