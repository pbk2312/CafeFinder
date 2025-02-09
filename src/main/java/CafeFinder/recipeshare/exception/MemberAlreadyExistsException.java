package CafeFinder.recipeshare.exception;

import static CafeFinder.recipeshare.util.ErrorMessage.Member_AlreadyExists;

public class MemberAlreadyExistsException extends RuntimeException {

    public MemberAlreadyExistsException() {
        super(Member_AlreadyExists.getMessage());
    }

}
