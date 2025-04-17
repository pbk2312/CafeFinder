package CafeFinder.cafe.member.controller;

import CafeFinder.cafe.global.dto.EmailDto;
import CafeFinder.cafe.global.dto.EmailVerifyDto;
import CafeFinder.cafe.global.dto.ResponseDto;
import CafeFinder.cafe.global.service.EmailService;

import static CafeFinder.cafe.global.util.ResponseMessage.EMAIL_SEND_SUCCESS;
import static CafeFinder.cafe.global.util.ResponseMessage.EMAIL_VERIFY_SUCCESS;

import CafeFinder.cafe.global.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
@Slf4j
public class EmailVerifyApiController {

    private final EmailService emailService;

    @PostMapping("/sendCode")
    public ResponseEntity<ResponseDto<String>> sendVerifyEmail(@Valid @RequestBody EmailDto emailDto) {
        emailService.sendVerificationCode(emailDto);
        return ResponseUtil.buildResponse(HttpStatus.ACCEPTED, EMAIL_SEND_SUCCESS.getMessage(), null, true);
    }

    @PostMapping("/verifyCode")
    public ResponseEntity<ResponseDto<String>> verifyEmail(@Valid @RequestBody EmailVerifyDto emailVerifyDto) {
        emailService.verifyCode(emailVerifyDto);
        return ResponseUtil.buildResponse(HttpStatus.OK, EMAIL_VERIFY_SUCCESS.getMessage(), null, true);
    }

}
