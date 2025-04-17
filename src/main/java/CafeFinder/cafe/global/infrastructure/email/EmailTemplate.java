package CafeFinder.cafe.global.infrastructure.email;

import java.text.MessageFormat;
import lombok.Getter;

@Getter
public enum EmailTemplate {

    VERIFICATION("이메일 인증 코드", "인증 코드는 {0}입니다.");

    private final String subject;
    private final String bodyTemplate;

    EmailTemplate(String subject, String bodyTemplate) {
        this.subject = subject;
        this.bodyTemplate = bodyTemplate;
    }

    public String getBody(String code) {
        return MessageFormat.format(bodyTemplate, code);
    }

}
