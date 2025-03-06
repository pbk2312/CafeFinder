package CafeFinder.cafe.controller;

import CafeFinder.cafe.dto.ResponseDto;
import CafeFinder.cafe.exception.CafeInfoNotFoundException;
import CafeFinder.cafe.exception.IncorrectPasswordException;
import CafeFinder.cafe.exception.MemberAlreadyExistsException;
import CafeFinder.cafe.exception.MemberNotFoundException;
import CafeFinder.cafe.exception.PasswordMismatchException;
import CafeFinder.cafe.exception.UnsupportedProviderException;
import CafeFinder.cafe.exception.VerifyCodeMisMatchException;
import CafeFinder.cafe.exception.YetVerifyEmailException;
import static CafeFinder.cafe.util.ErrorMessage.CAFE_INFO_NOT_FOUND;
import static CafeFinder.cafe.util.ErrorMessage.MEMBER_NOT_FOUND;
import static CafeFinder.cafe.util.ErrorMessage.Member_AlreadyExists;
import static CafeFinder.cafe.util.ErrorMessage.NOT_VERIFY_CODE;
import static CafeFinder.cafe.util.ErrorMessage.PASSWORDS_DO_NOT_MATCH;
import static CafeFinder.cafe.util.ErrorMessage.PASSWORD_INCORRECT;
import static CafeFinder.cafe.util.ErrorMessage.SERVER_ERROR;
import static CafeFinder.cafe.util.ErrorMessage.UNSUPPORTEDPROVIDER;
import static CafeFinder.cafe.util.ErrorMessage.VALIDATION_FAILED;
import static CafeFinder.cafe.util.ErrorMessage.VERIFY_CODE_MIS_MATCH;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    private <T> ResponseEntity<ResponseDto<T>> buildResponse(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status).body(new ResponseDto<>(message, data, false));
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ResponseDto<String>> handleMemberNotFoundException(MemberNotFoundException e) {
        log.error("MemberNotFoundException: {}", e.getMessage(), e);
        return buildResponse(HttpStatus.NOT_FOUND, MEMBER_NOT_FOUND.getMessage(), null);
    }

    @ExceptionHandler(MemberAlreadyExistsException.class)
    public ResponseEntity<ResponseDto<String>> handleMemberAlreadyExistsException(MemberAlreadyExistsException e) {
        log.error("MemberAlreadyExistsException: {}", e.getMessage(), e);
        return buildResponse(HttpStatus.CONFLICT, Member_AlreadyExists.getMessage(), null);
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

    @ExceptionHandler(VerifyCodeMisMatchException.class)
    public ResponseEntity<ResponseDto<String>> handleVerifyCodeMisMatchException(
            VerifyCodeMisMatchException ex
    ) {
        log.info("VerifyCodeMisMatchException :{}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, VERIFY_CODE_MIS_MATCH.getMessage(), null);
    }

    @ExceptionHandler(YetVerifyEmailException.class)
    public ResponseEntity<ResponseDto<String>> handleAlreadyVerifyEmailException(
            VerifyCodeMisMatchException ex
    ) {
        log.info("handleAlreadyVerifyEmailException :{}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, NOT_VERIFY_CODE.getMessage(), null);
    }

    @ExceptionHandler(UnsupportedProviderException.class)
    public ResponseEntity<ResponseDto<String>> handleAlreadyUnsupportedProviderException(
            VerifyCodeMisMatchException ex
    ) {
        log.info("UnsupportedProviderException :{}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_ACCEPTABLE, UNSUPPORTEDPROVIDER.getMessage(), null);
    }

    @ExceptionHandler(CafeInfoNotFoundException.class)
    public ResponseEntity<ResponseDto<String>> handleCafeInfoNotFoundException(CafeInfoNotFoundException e) {
        log.error("CafeInfoNotFoundException: {}", e.getMessage(), e);
        return buildResponse(HttpStatus.NOT_FOUND, CAFE_INFO_NOT_FOUND.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<String>> handleGeneralException(Exception e) {
        log.error("예기치 못한 오류 발생: {}", e.getMessage(), e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, SERVER_ERROR.getMessage(), null);
    }

}
