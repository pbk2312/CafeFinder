package CafeFinder.cafe.util;

import CafeFinder.cafe.dto.ResponseDto;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {


    private ResponseUtil() {
        // 인스턴스화 방지
    }

    public static <T> ResponseEntity<ResponseDto<T>> buildResponse(String message, T data, boolean success) {
        return ResponseEntity.ok(new ResponseDto<>(message, data, success));
    }

}
