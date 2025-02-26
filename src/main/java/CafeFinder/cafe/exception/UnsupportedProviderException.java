package CafeFinder.cafe.exception;

import static CafeFinder.cafe.util.ErrorMessage.UNSUPPORTEDPROVIDER;

public class UnsupportedProviderException extends RuntimeException {
    public UnsupportedProviderException(String message) {
        super(UNSUPPORTEDPROVIDER.getMessage() + message);
    }
}
