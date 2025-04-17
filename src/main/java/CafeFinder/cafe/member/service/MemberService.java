package CafeFinder.cafe.member.service;

import CafeFinder.cafe.member.domain.Member;
import CafeFinder.cafe.member.dto.MemberSignUpDto;

public interface MemberService {

    void save(MemberSignUpDto memberSignUpDto);

    Member getMemberByEmail(String email);
    
    Member getMemberByToken(String accessToken);


}
