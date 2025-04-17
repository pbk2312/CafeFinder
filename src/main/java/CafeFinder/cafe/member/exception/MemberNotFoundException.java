package CafeFinder.cafe.member.exception;

import static CafeFinder.cafe.global.util.ErrorMessage.MEMBER_NOT_FOUND;

public class MemberNotFoundException extends RuntimeException {


    public MemberNotFoundException() {
        super(MEMBER_NOT_FOUND.getMessage());
    }

}
