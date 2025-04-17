package CafeFinder.cafe.member.exception;

import static CafeFinder.cafe.global.util.ErrorMessage.UNSUPPORTEDPROVIDER;

public class UnavailableProviderException extends RuntimeException {
    public UnavailableProviderException(String message) {
        super(UNSUPPORTEDPROVIDER.getMessage() + message);
    }
}
