package CafeFinder.cafe.member.security.util;

import static CafeFinder.cafe.global.exception.ErrorCode.LOGIN_REQUIRED;

import CafeFinder.cafe.global.exception.ErrorException;
import CafeFinder.cafe.member.security.jwt.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {

    private SecurityUtil() {
    }

    public static Long getMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication = {}", authentication);
        if (authentication == null
            || !(authentication.getPrincipal() instanceof CustomUserDetails principal)) {
            throw new ErrorException(LOGIN_REQUIRED);
        }
        return principal.getMemberId();
    }

}
