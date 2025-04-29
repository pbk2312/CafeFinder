package CafeFinder.cafe.global.exception;

import lombok.Getter;

@Getter
public class ErrorException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String url;

    public ErrorException(ErrorCode errorCode, String url) {
        this.errorCode = errorCode;
        this.url = url;
    }

    public ErrorException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.url = null;
    }
}
