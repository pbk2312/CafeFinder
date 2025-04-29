package CafeFinder.cafe.member.validator;

import static CafeFinder.cafe.global.exception.ErrorCode.INVALID_PASSWORD_MATCH;
import static CafeFinder.cafe.global.exception.ErrorCode.MEMBER_ALREADY_EXISTS;

import CafeFinder.cafe.global.exception.ErrorException;
import CafeFinder.cafe.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class MemberValidator {

    private final MemberRepository memberRepository;

    public static void validatePassword(String password, String checkPassword) {
        if (!password.equals(checkPassword)) {
            throw new ErrorException(INVALID_PASSWORD_MATCH);
        }
    }

    public void isMemberExists(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new ErrorException(MEMBER_ALREADY_EXISTS);
        }
    }

}
