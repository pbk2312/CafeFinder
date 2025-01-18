package recipe.recipeshare.controller.restapi;

import static recipe.recipeshare.util.ViewMessage.EMAIL_SEND_SUCCESS;
import static recipe.recipeshare.util.ViewMessage.EMAIL_VERIFY_SUCCESS;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import recipe.recipeshare.dto.EmailDto;
import recipe.recipeshare.dto.EmailVerifyDto;
import recipe.recipeshare.dto.ResponseDto;
import recipe.recipeshare.service.member.EmailService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
@Log4j2
public class EmailVerifyApiController {

    private final EmailService emailService;

    @PostMapping("/sendCode")
    public ResponseEntity<ResponseDto<String>> sendVerifyEmail(
            @Valid @RequestBody EmailDto emailDto
    ) {
        emailService.sendVerificationCode(emailDto);
        return ResponseEntity.ok(new ResponseDto<>(EMAIL_SEND_SUCCESS.getMessage(), null, true));
    }

    @PostMapping("/verifyCode")
    public ResponseEntity<ResponseDto<String>> verifyEmail(@Valid @RequestBody EmailVerifyDto emailVerifyDto) {
        log.info(emailVerifyDto.getEmail());
        emailService.verifyCode(emailVerifyDto);
        return ResponseEntity.ok(new ResponseDto<>(EMAIL_VERIFY_SUCCESS.getMessage(), null, true));
    }

}
