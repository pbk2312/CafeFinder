package recipe.recipeshare.exception;

import static recipe.recipeshare.util.ErrorMessage.MEMBER_NOT_FOUND;

public class MemberNotFoundException extends RuntimeException {


    public MemberNotFoundException() {
        super(MEMBER_NOT_FOUND.getMessage());
    }

}
