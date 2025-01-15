package recipe.recipeshare.validator;

import recipe.recipeshare.exception.InvalidPasswordException;

public class MemberValidator {


    public static void validatePassword(String password, String checkPassword) {
        if (!password.equals(checkPassword)) {
            throw new InvalidPasswordException();
        }
    }

}
