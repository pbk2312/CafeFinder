package CafeFinder.cafe.global.service;

import static CafeFinder.cafe.global.exception.ErrorCode.VERIFY_CODE_MISMATCH;

import CafeFinder.cafe.global.dto.EmailDto;
import CafeFinder.cafe.global.dto.EmailVerifyDto;
import CafeFinder.cafe.global.exception.ErrorException;
import CafeFinder.cafe.global.infrastructure.email.EmailSender;
import CafeFinder.cafe.global.infrastructure.email.EmailTemplate;
import CafeFinder.cafe.global.infrastructure.redis.RedisEmailVerifyService;
import CafeFinder.cafe.member.util.VeritificationCodeGenerator;
import CafeFinder.cafe.member.validator.MemberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final EmailSender emailSender;
    private final MemberValidator memberValidator;
    private final RedisEmailVerifyService redisEmailVerifyService;

    @Override
    public void sendVerificationCode(EmailDto emailDto) {
        String email = emailDto.getEmail();
        log.info("인증번호 이메일 전송 시작: {}", email);

        validateMember(email);
        String verificationCode = generateVerificationCode();
        storeVerificationCodeToRedis(email, verificationCode);
        emailSend(email, verificationCode);

        log.info("이메일 발송 성공: {}", email);

    }

    @Override
    public void verifyCode(EmailVerifyDto emailVerifyDto) {
        String email = emailVerifyDto.getEmail();
        log.info("인증 코드 인증 시작: {}", email);

        String storedCode = retrieveStoredCode(email);
        checkVerificationCode(emailVerifyDto.getCode(), storedCode);
        updateVerificationStatus(email);

        log.info("인증 코드 인증 성공 및 Redis 상태 변경 완료: {}", email);
    }

    private void validateMember(String email) {
        memberValidator.isMemberExists(email);
    }

    private String generateVerificationCode() {
        return VeritificationCodeGenerator.generateVerificationCode();
    }

    private void storeVerificationCodeToRedis(String email, String verificationCode) {
        redisEmailVerifyService.saveVerificationCode(email, verificationCode);
    }

    private void emailSend(String email, String verificationCode) {
        String subject = EmailTemplate.VERIFICATION.getSubject();
        String body = EmailTemplate.VERIFICATION.getBody(verificationCode);
        emailSender.sendEmail(email, subject, body);
    }

    private String retrieveStoredCode(String email) {
        return redisEmailVerifyService.getVerificationCode(email);
    }

    private void checkVerificationCode(String providedCode, String storedCode) {
        if (!providedCode.equals(storedCode)) {
            throw new ErrorException(VERIFY_CODE_MISMATCH);
        }
    }

    private void updateVerificationStatus(String email) {
        redisEmailVerifyService.changeStatus(email);
    }

}
