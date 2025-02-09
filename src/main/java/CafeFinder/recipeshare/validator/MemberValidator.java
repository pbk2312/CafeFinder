package CafeFinder.recipeshare.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import CafeFinder.recipeshare.exception.MemberAlreadyExistsException;
import CafeFinder.recipeshare.exception.PasswordMismatchException;
import CafeFinder.recipeshare.repository.MemberRepository;

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
