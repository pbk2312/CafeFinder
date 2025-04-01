package CafeFinder.cafe.service.interfaces;

import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.dto.MemberSignUpDto;

public interface MemberService {

    void save(MemberSignUpDto memberSignUpDto);

    Member getMemberByEmail(String email);


    Member getMemberByToken(String accessToken);


}
