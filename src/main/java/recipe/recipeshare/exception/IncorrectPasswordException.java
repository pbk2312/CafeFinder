package recipe.recipeshare.exception;

import static recipe.recipeshare.util.ErrorMessage.PASSWORD_INCORRECT;

public class IncorrectPasswordException extends RuntimeException {

    public IncorrectPasswordException() {
        super(PASSWORD_INCORRECT.getMessage());
    }

}
