package CafeFinder.cafe.global.exception;

import CafeFinder.cafe.global.dto.ResponseDto;
import CafeFinder.cafe.global.util.ResponseUtil;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<ResponseDto<ErrorPayload>> handleErrorException(ErrorException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        log.error("ErrorException - code: {} message: {} url: {}",
            errorCode.getCode(), errorCode.getMessage(), ex.getUrl(), ex);

        ErrorPayload payload = new ErrorPayload(
            errorCode.getCode(),
            errorCode.getMessage(),
            ex.getUrl()
        );

        HttpStatus httpStatus = switch (errorCode.getErrorStatus()) {
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        return ResponseUtil.buildResponse(
            httpStatus,
            errorCode.getMessage(),
            payload,
            false
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<String>> handleValidationException(
        MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " +
                (err.getDefaultMessage() != null ? err.getDefaultMessage() : "알 수 없는 오류"))
            .collect(Collectors.joining("\n"));

        log.warn("Validation failed: {}", errors);
        return ResponseUtil.buildResponse(
            HttpStatus.BAD_REQUEST,
            errors,
            errors,
            false
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<String>> handleGeneralException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return ResponseUtil.buildResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ErrorCode.SERVER_ERROR.getMessage(),
            ErrorCode.SERVER_ERROR.getMessage(),
            false
        );
    }

    public record ErrorPayload(String code, String message, String url) {

    }
}
