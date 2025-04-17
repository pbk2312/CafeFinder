package CafeFinder.cafe.member.exception;

import static CafeFinder.cafe.global.util.ErrorMessage.Member_AlreadyExists;

public class MemberAlreadyExistsException extends RuntimeException {

    public MemberAlreadyExistsException() {
        super(Member_AlreadyExists.getMessage());
    }

}
