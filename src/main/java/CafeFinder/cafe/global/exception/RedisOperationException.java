package CafeFinder.cafe.global.exception;

import static CafeFinder.cafe.global.util.ErrorMessage.REDIS_ERROR;

public class RedisOperationException extends RuntimeException {
    public RedisOperationException(String memberId, String cafeCode) {
        super(String.format(REDIS_ERROR.getMessage(), memberId, cafeCode));
    }
}
