package CafeFinder.cafe.exception;

import static CafeFinder.cafe.util.ErrorMessage.INVALID_LOCATION_FORMAT;

public class InvalidLocationFormatException extends RuntimeException {

    public InvalidLocationFormatException() {
        super(INVALID_LOCATION_FORMAT.getMessage());
    }

}
