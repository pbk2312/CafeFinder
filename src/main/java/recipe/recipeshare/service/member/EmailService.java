package recipe.recipeshare.service.member;


import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import recipe.recipeshare.dto.EmailDto;
import recipe.recipeshare.dto.EmailVerifyDto;
import recipe.recipeshare.exception.VerifyCodeMisMatchException;
import recipe.recipeshare.validator.MemberValidator;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmailService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender mailSender;
    private final MemberValidator memberValidator;

    public void sendVerificationCode(EmailDto emailDto) {

        log.info("인증번호 이메일 전송 : {} ", emailDto.getEmail());
        memberValidator.isMemberExists(emailDto.getEmail());
        String verificationCode = generateVerificationCode();

        // 유효시간 5분
        redisTemplate.opsForValue().set(emailDto.getEmail(), verificationCode, 5, TimeUnit.MINUTES);

        // 이메일 발송
        sendEmail(emailDto.getEmail(), verificationCode);
        log.info("이메일 발송 성공 : {} ", emailDto.getEmail());

    }

    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000); // 6자리 랜덤
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
        String storedCode = redisTemplate.opsForValue().get(emailVerifyDto.getEmail());
        boolean isMatch = emailVerifyDto.getCode().equals(storedCode);

        if (!isMatch) {
            throw new VerifyCodeMisMatchException();
        }

        // 인증 성공 시 Redis에 인증 상태 저장 (TTL 설정: 30분)
        redisTemplate.opsForValue().set("verified:" + emailVerifyDto.getEmail(), "true", 30, TimeUnit.MINUTES);
        log.info("인증 코드 인증 성공 및 Redis 삭제 완료 : {}", emailVerifyDto.getEmail());
    }

}
