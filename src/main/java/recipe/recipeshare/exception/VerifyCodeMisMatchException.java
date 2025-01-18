package recipe.recipeshare.exception;

import static recipe.recipeshare.util.ErrorMessage.VERIFY_CODE_MIS_MATCH;

public class VerifyCodeMisMatchException extends RuntimeException {

    public VerifyCodeMisMatchException() {
        super(VERIFY_CODE_MIS_MATCH.getMessage());
    }

}
