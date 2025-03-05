package CafeFinder.cafe.service.member;


import CafeFinder.cafe.dto.EmailDto;
import CafeFinder.cafe.dto.EmailVerifyDto;
import CafeFinder.cafe.exception.VerifyCodeMisMatchException;
import CafeFinder.cafe.service.redis.RedisEmailVerifyService;
import CafeFinder.cafe.validator.MemberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmailService {

    private final JavaMailSender mailSender;
    private final MemberValidator memberValidator;
    private final RedisEmailVerifyService redisEmailVerifyService;

    public void sendVerificationCode(EmailDto emailDto) {

        log.info("인증번호 이메일 전송 : {} ", emailDto.getEmail());
        memberValidator.isMemberExists(emailDto.getEmail());
        String verificationCode = generateVerificationCode();

        redisEmailVerifyService.saveVerificationCode(emailDto.getEmail(), verificationCode);

        // 이메일 발송
        sendEmail(emailDto.getEmail(), verificationCode);
        log.info("이메일 발송 성공 : {} ", emailDto.getEmail());

    }

    private void sendEmail(String email, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("이메일 인증 코드");
        message.setText("인증 코드는 " + verificationCode + "입니다.");
        mailSender.send(message);
    }

    public void verifyCode(EmailVerifyDto emailVerifyDto) {
        log.info("인증 코드 인증 시작 : {}", emailVerifyDto.getEmail());
        String storedCode = redisEmailVerifyService.getVerificationCode(emailVerifyDto.getEmail());
        boolean isMatch = emailVerifyDto.getCode().equals(storedCode);

        if (!isMatch) {
            throw new VerifyCodeMisMatchException();
        }

        redisEmailVerifyService.changeStatus(emailVerifyDto.getEmail());
        log.info("인증 코드 인증 성공 및 Redis 삭제 완료 : {}", emailVerifyDto.getEmail());
    }

    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000); // 6자리 랜덤
    }

}
