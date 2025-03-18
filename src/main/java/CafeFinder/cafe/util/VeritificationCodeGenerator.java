package CafeFinder.cafe.util;

public class VeritificationCodeGenerator {

    private static final int VERIFICATION_CODE_MIN = 100000;
    private static final int VERIFICATION_CODE_RANGE = 900000;

    private VeritificationCodeGenerator() {
        // 인스턴스화 방지
    }

    public static String generateVerificationCode() {
        int code = (int) (Math.random() * VERIFICATION_CODE_RANGE) + VERIFICATION_CODE_MIN;
        return String.valueOf(code);
    }

}
