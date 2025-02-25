package CafeFinder.cafe.exception;

import static CafeFinder.cafe.util.ErrorMessage.UnsupportedProvider;

public class UnsupportedProviderException extends RuntimeException {
    public UnsupportedProviderException(String message) {
        super(UnsupportedProvider.getMessage() + message);
    }
}
