package CafeFinder.cafe.member.service;

import CafeFinder.cafe.member.dto.MemberProfileDto;
import CafeFinder.cafe.member.dto.ProfileDto;

public interface ProfileService {


    ProfileDto getProfileByToken();

    MemberProfileDto getMemberInfo();


}
