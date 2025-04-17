package CafeFinder.cafe.member.validator;

import CafeFinder.cafe.member.exception.MemberAlreadyExistsException;
import CafeFinder.cafe.member.exception.PasswordConfirmationMisMatch;
import CafeFinder.cafe.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class MemberValidator {

    private final MemberRepository memberRepository;

    public static void validatePassword(String password, String checkPassword) {
        if (!password.equals(checkPassword)) {
            throw new PasswordConfirmationMisMatch();
        }
    }

    public void isMemberExists(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new MemberAlreadyExistsException();
        }
    }

}
