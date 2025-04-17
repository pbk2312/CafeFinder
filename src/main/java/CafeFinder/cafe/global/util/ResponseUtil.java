package CafeFinder.cafe.global.util;

import CafeFinder.cafe.global.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    private ResponseUtil() {
        // 인스턴스화 방지
    }

    public static <T> ResponseEntity<ResponseDto<T>> buildResponse(HttpStatus status, String message, T data,
                                                                   boolean success) {
        return ResponseEntity.status(status).body(new ResponseDto<>(message, data, success));
    }

}
