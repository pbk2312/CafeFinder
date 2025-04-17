package CafeFinder.cafe.global.service;

import CafeFinder.cafe.global.dto.EmailDto;
import CafeFinder.cafe.global.dto.EmailVerifyDto;

public interface EmailService {

    void sendVerificationCode(EmailDto emailDto);

    void verifyCode(EmailVerifyDto emailVerifyDto);

}
