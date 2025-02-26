package CafeFinder.cafe.exception;

import static CafeFinder.cafe.util.ErrorMessage.PROFILESAVEEXCEPTION;

public class ProfileFileException extends RuntimeException {

    public ProfileFileException() {
        super(PROFILESAVEEXCEPTION.getMessage());
    }

}
