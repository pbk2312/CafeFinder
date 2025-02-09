package CafeFinder.cafe.exception;

import static CafeFinder.cafe.util.ErrorMessage.Member_AlreadyExists;

public class MemberAlreadyExistsException extends RuntimeException {

    public MemberAlreadyExistsException() {
        super(Member_AlreadyExists.getMessage());
    }

}
