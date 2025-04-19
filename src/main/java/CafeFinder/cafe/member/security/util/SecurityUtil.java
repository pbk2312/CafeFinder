package CafeFinder.cafe.member.security.util;

import CafeFinder.cafe.member.exception.UnauthorizedException;
import CafeFinder.cafe.member.security.jwt.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() {
    }

    public static Long getMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails principal)) {
            throw new UnauthorizedException();
        }
        return principal.getMemberId();
    }

}
