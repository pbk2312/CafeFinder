package CafeFinder.cafe.controller;

import CafeFinder.cafe.dto.ResponseDto;
import CafeFinder.cafe.exception.CafeNotFoundException;
import CafeFinder.cafe.exception.IncorrectPasswordException;
import CafeFinder.cafe.exception.MemberAlreadyExistsException;
import CafeFinder.cafe.exception.MemberNotFoundException;
import CafeFinder.cafe.exception.PasswordConfirmationMisMatch;
import CafeFinder.cafe.exception.UnsupportedProviderException;
import CafeFinder.cafe.exception.VerifyCodeMisMatchException;
import CafeFinder.cafe.exception.YetVerifyEmailException;

import static CafeFinder.cafe.util.ErrorMessage.CAFE_INFO_NOT_FOUND;
import static CafeFinder.cafe.util.ErrorMessage.MEMBER_NOT_FOUND;
import static CafeFinder.cafe.util.ErrorMessage.Member_AlreadyExists;
import static CafeFinder.cafe.util.ErrorMessage.NOT_VERIFY_CODE;
import static CafeFinder.cafe.util.ErrorMessage.PASSWORD_CONFIRMATION_MISMATCH;
import static CafeFinder.cafe.util.ErrorMessage.PASSWORD_INCORRECT;
import static CafeFinder.cafe.util.ErrorMessage.SERVER_ERROR;
import static CafeFinder.cafe.util.ErrorMessage.UNSUPPORTEDPROVIDER;
import static CafeFinder.cafe.util.ErrorMessage.VALIDATION_FAILED;
import static CafeFinder.cafe.util.ErrorMessage.VERIFY_CODE_MIS_MATCH;

import CafeFinder.cafe.util.ResponseUtil;
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

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ResponseDto<String>> handleMemberNotFoundException(MemberNotFoundException e) {
        log.error("MemberNotFoundException: {}", e.getMessage(), e);
        return ResponseUtil.buildResponse(HttpStatus.NOT_FOUND, MEMBER_NOT_FOUND.getMessage(), null, false);
    }

    @ExceptionHandler(MemberAlreadyExistsException.class)
    public ResponseEntity<ResponseDto<String>> handleMemberAlreadyExistsException(MemberAlreadyExistsException e) {
        log.error("MemberAlreadyExistsException: {}", e.getMessage(), e);
        return ResponseUtil.buildResponse(HttpStatus.CONFLICT, Member_AlreadyExists.getMessage(), null, false);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ResponseDto<String>> handleIncorrectPasswordException(IncorrectPasswordException e) {
        log.error("IncorrectPasswordException: {}", e.getMessage(), e);
        return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, PASSWORD_INCORRECT.getMessage(), null, false);
    }

    @ExceptionHandler(PasswordConfirmationMisMatch.class)
    public ResponseEntity<ResponseDto<String>> handlePasswordConfirmationMisMatch(PasswordConfirmationMisMatch e) {
        log.error("PasswordConfirmationMisMatch: {}", e.getMessage(), e);
        return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, PASSWORD_CONFIRMATION_MISMATCH.getMessage(), null,
                false);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " +
                        (error.getDefaultMessage() != null ? error.getDefaultMessage() : "알 수 없는 오류"))
                .collect(Collectors.joining("\n"));
        return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, VALIDATION_FAILED.getMessage(), errors, false);
    }

    @ExceptionHandler(VerifyCodeMisMatchException.class)
    public ResponseEntity<ResponseDto<String>> handleVerifyCodeMisMatchException(VerifyCodeMisMatchException ex) {
        log.info("VerifyCodeMisMatchException :{}", ex.getMessage());
        return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, VERIFY_CODE_MIS_MATCH.getMessage(), null, false);
    }

    @ExceptionHandler(YetVerifyEmailException.class)
    public ResponseEntity<ResponseDto<String>> handleYetVerifyEmailException(YetVerifyEmailException ex) {
        log.info("YetVerifyEmailException :{}", ex.getMessage());
        return ResponseUtil.buildResponse(HttpStatus.FORBIDDEN, NOT_VERIFY_CODE.getMessage(), null, false);
    }

    @ExceptionHandler(UnsupportedProviderException.class)
    public ResponseEntity<ResponseDto<String>> handleUnsupportedProviderException(UnsupportedProviderException ex) {
        log.info("UnsupportedProviderException :{}", ex.getMessage());
        return ResponseUtil.buildResponse(HttpStatus.NOT_ACCEPTABLE, UNSUPPORTEDPROVIDER.getMessage(), null, false);
    }

    @ExceptionHandler(CafeNotFoundException.class)
    public ResponseEntity<ResponseDto<String>> handleCafeInfoNotFoundException(CafeNotFoundException e) {
        log.error("CafeInfoNotFoundException: {}", e.getMessage(), e);
        return ResponseUtil.buildResponse(HttpStatus.NOT_FOUND, CAFE_INFO_NOT_FOUND.getMessage(), null, false);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<String>> handleGeneralException(Exception e) {
        log.error("예기치 못한 오류 발생: {}", e.getMessage(), e);
        return ResponseUtil.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, SERVER_ERROR.getMessage(), null, false);
    }

}
