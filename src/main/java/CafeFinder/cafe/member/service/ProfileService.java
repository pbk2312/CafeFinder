package CafeFinder.cafe.member.service;

import CafeFinder.cafe.member.dto.AccessTokenDto;
import CafeFinder.cafe.member.dto.MemberProfileDto;
import CafeFinder.cafe.member.dto.MemberUpdateDto;
import CafeFinder.cafe.member.dto.ProfileDto;

public interface ProfileService {

    void update(MemberUpdateDto memberUpdateDto, AccessTokenDto accessTokenDto);


    ProfileDto getProfileByToken(AccessTokenDto accessTokenDto);

    MemberProfileDto getUserInfoByToken(AccessTokenDto accessTokenDto);


}
