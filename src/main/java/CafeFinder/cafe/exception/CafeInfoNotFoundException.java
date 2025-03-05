package CafeFinder.cafe.exception;

import static CafeFinder.cafe.util.ErrorMessage.CAFE_INFO_NOT_FOUND;

public class CafeInfoNotFoundException extends RuntimeException {

    public CafeInfoNotFoundException(String cadeCode) {
        super(CAFE_INFO_NOT_FOUND.getMessage() + " " + cadeCode);
    }

}
