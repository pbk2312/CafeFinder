package CafeFinder.cafe.exception;

import CafeFinder.cafe.util.ErrorMessage;

public class WrongDistrictAndTheme extends RuntimeException {

    public WrongDistrictAndTheme() {
        super(ErrorMessage.WRONG_DISTRCIT_THEME.getMessage());
    }

}
