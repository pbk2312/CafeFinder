package CafeFinder.cafe.cafe.exception;

import static CafeFinder.cafe.global.util.ErrorMessage.INVALID_LOCATION_FORMAT;

public class InvalidLocationFormatException extends RuntimeException {

    public InvalidLocationFormatException() {
        super(INVALID_LOCATION_FORMAT.getMessage());
    }

}
