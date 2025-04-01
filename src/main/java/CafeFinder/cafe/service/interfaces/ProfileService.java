package CafeFinder.cafe.service.interfaces;

import CafeFinder.cafe.dto.AccessTokenDto;
import CafeFinder.cafe.dto.MemberProfileDto;
import CafeFinder.cafe.dto.MemberUpdateDto;
import CafeFinder.cafe.dto.ProfileDto;

public interface ProfileService {

    void update(MemberUpdateDto memberUpdateDto, AccessTokenDto accessTokenDto);


    ProfileDto getProfileByToken(AccessTokenDto accessTokenDto);

    MemberProfileDto getUserInfoByToken(AccessTokenDto accessTokenDto);


}
