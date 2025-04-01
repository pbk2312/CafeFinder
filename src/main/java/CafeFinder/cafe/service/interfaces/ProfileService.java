package CafeFinder.cafe.service.interfaces;

import CafeFinder.cafe.dto.MemberProfileDto;
import CafeFinder.cafe.dto.MemberUpdateDto;
import CafeFinder.cafe.dto.ProfileDto;

public interface ProfileService {

    void update(MemberUpdateDto memberUpdateDto, String accessToken);


    ProfileDto getProfileByToken(String accessToken);

    MemberProfileDto getUserInfoByToken(String accessToken);


}
