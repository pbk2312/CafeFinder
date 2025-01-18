package recipe.recipeshare.exception;

import static recipe.recipeshare.util.ErrorMessage.Member_AlreadyExists;

public class MemberAlreadyExistsException extends RuntimeException {

    public MemberAlreadyExistsException() {
        super(Member_AlreadyExists.getMessage());
    }

}
