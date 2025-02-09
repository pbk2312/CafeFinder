package CafeFinder.cafe.exception;

import static CafeFinder.cafe.util.ErrorMessage.VERIFY_CODE_MIS_MATCH;

public class VerifyCodeMisMatchException extends RuntimeException {

    public VerifyCodeMisMatchException() {
        super(VERIFY_CODE_MIS_MATCH.getMessage());
    }

}
