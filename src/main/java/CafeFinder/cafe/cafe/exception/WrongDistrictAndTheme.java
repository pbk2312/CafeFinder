package CafeFinder.cafe.cafe.exception;

import CafeFinder.cafe.global.util.ErrorMessage;

public class WrongDistrictAndTheme extends RuntimeException {

    public WrongDistrictAndTheme() {
        super(ErrorMessage.WRONG_DISTRCIT_THEME.getMessage());
    }

}
