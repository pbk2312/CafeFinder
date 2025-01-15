package recipe.recipeshare.exception;

public class InvalidPasswordException extends RuntimeException {

    private static final String MESSAGE = "비밀번호가 틀립니다.";

    public InvalidPasswordException() {
        super(MESSAGE);
    }

}
