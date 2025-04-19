package CafeFinder.cafe.member.service;

import CafeFinder.cafe.member.dto.MemberProfileDto;
import CafeFinder.cafe.member.dto.MemberUpdateDto;
import CafeFinder.cafe.member.dto.ProfileDto;

public interface ProfileService {

    void update(MemberUpdateDto memberUpdateDto);


    ProfileDto getProfileByToken();

    MemberProfileDto getMemberInfo();


}
