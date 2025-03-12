package CafeFinder.cafe.validator;

import CafeFinder.cafe.exception.MemberAlreadyExistsException;
import CafeFinder.cafe.exception.PasswordMismatchException;
import CafeFinder.cafe.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


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
