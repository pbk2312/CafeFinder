package CafeFinder.cafe.cafe.exception;

import static CafeFinder.cafe.global.util.ErrorMessage.CAFE_INFO_NOT_FOUND;

public class CafeNotFoundException extends RuntimeException {

    public CafeNotFoundException(String cadeCode) {
        super(CAFE_INFO_NOT_FOUND.getMessage() + " " + cadeCode);
    }

}
