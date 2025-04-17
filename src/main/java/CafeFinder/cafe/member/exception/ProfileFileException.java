package CafeFinder.cafe.member.exception;

import static CafeFinder.cafe.global.util.ErrorMessage.PROFILESAVEEXCEPTION;

public class ProfileFileException extends RuntimeException {

    public ProfileFileException() {
        super(PROFILESAVEEXCEPTION.getMessage());
    }

}
