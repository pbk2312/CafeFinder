package CafeFinder.cafe.member.exception;

import static CafeFinder.cafe.global.util.ErrorMessage.VERIFY_CODE_MIS_MATCH;

public class VerifyCodeMisMatchException extends RuntimeException {

    public VerifyCodeMisMatchException() {
        super(VERIFY_CODE_MIS_MATCH.getMessage());
    }

}
