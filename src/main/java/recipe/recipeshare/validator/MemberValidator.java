package recipe.recipeshare.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import recipe.recipeshare.exception.MemberAlreadyExistsException;
import recipe.recipeshare.exception.PasswordMismatchException;
import recipe.recipeshare.repository.MemberRepository;

@RequiredArgsConstructor
@Component
public class MemberValidator {

    private final MemberRepository memberRepository;

    public static void validatePassword(String password, String checkPassword) {
        if (!password.equals(checkPassword)) {
            throw new PasswordMismatchException();
        }
    }

    public void isMemberExists(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new MemberAlreadyExistsException();
        }
    }

}
