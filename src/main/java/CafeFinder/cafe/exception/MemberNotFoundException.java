package CafeFinder.cafe.exception;

import static CafeFinder.cafe.util.ErrorMessage.MEMBER_NOT_FOUND;

public class MemberNotFoundException extends RuntimeException {


    public MemberNotFoundException() {
        super(MEMBER_NOT_FOUND.getMessage());
    }

}
