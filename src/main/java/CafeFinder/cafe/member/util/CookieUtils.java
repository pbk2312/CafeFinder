package CafeFinder.cafe.member.util;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtils {

    private CookieUtils() {
        // 인스턴스화 방지
    }

    public static void addCookie(HttpServletResponse response, String name, String value,
        int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        // cookie.setSecure(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static void removeCookie(HttpServletResponse response, String name) {
        addCookie(response, name, null, 0);
    }

}
