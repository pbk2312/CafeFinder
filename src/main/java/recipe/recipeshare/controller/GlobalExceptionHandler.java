package recipe.recipeshare.controller;

import static recipe.recipeshare.util.ErrorMessage.MEMBER_NOT_FOUND;
import static recipe.recipeshare.util.ErrorMessage.PASSWORDS_DO_NOT_MATCH;
import static recipe.recipeshare.util.ErrorMessage.PASSWORD_INCORRECT;
import static recipe.recipeshare.util.ErrorMessage.SERVER_ERROR;
import static recipe.recipeshare.util.ErrorMessage.VALIDATION_FAILED;

import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import recipe.recipeshare.dto.ResponseDto;
import recipe.recipeshare.exception.IncorrectPasswordException;
import recipe.recipeshare.exception.MemberNotFoundException;
import recipe.recipeshare.exception.PasswordMismatchException;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    private <T> ResponseEntity<ResponseDto<T>> buildResponse(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status).body(new ResponseDto<>(message, data, false));
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ResponseDto<String>> handleMemberNotFoundException(MemberNotFoundException e) {
        log.error("MemberNotFoundException: {}", e.getMessage(), e);
        return buildResponse(HttpStatus.UNAUTHORIZED, MEMBER_NOT_FOUND.getMessage(), null);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ResponseDto<String>> handleIncorrectPasswordException(IncorrectPasswordException e) {
        log.error("IncorrectPasswordException: {}", e.getMessage(), e);
        return buildResponse(HttpStatus.UNAUTHORIZED, PASSWORD_INCORRECT.getMessage(), null);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ResponseDto<String>> handlePasswordMismatchException(PasswordMismatchException e) {
        log.error("PasswordMismatchException: {}", e.getMessage(), e);
        return buildResponse(HttpStatus.BAD_REQUEST, PASSWORDS_DO_NOT_MATCH.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " +
                        (error.getDefaultMessage() != null ? error.getDefaultMessage() : "알 수 없는 오류"))
                .collect(Collectors.joining("\n"));
        return buildResponse(HttpStatus.BAD_REQUEST, VALIDATION_FAILED.getMessage(), errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<String>> handleGeneralException(Exception e) {
        log.error("예기치 못한 오류 발생: {}", e.getMessage(), e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, SERVER_ERROR.getMessage(), null);
    }

}
