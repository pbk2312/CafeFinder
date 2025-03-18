package CafeFinder.cafe.service.interfaces;

import CafeFinder.cafe.dto.EmailDto;
import CafeFinder.cafe.dto.EmailVerifyDto;

public interface EmailService {

    void sendVerificationCode(EmailDto emailDto);

    void verifyCode(EmailVerifyDto emailVerifyDto);

}
