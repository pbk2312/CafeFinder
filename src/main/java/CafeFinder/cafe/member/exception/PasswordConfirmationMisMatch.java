package CafeFinder.cafe.member.exception;

import static CafeFinder.cafe.global.util.ErrorMessage.PASSWORD_CONFIRMATION_MISMATCH;

public class PasswordConfirmationMisMatch extends RuntimeException {


    public PasswordConfirmationMisMatch() {
        super(PASSWORD_CONFIRMATION_MISMATCH.getMessage());
    }

}
