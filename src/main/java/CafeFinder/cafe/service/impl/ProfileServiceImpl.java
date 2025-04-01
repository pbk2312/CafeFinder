package CafeFinder.cafe.service.impl;

import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.dto.AccessTokenDto;
import CafeFinder.cafe.dto.MemberProfileDto;
import CafeFinder.cafe.dto.MemberUpdateDto;
import CafeFinder.cafe.dto.ProfileDto;
import CafeFinder.cafe.service.interfaces.FileService;
import CafeFinder.cafe.service.interfaces.MemberService;
import CafeFinder.cafe.service.interfaces.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    @Value("${cafe.profile.image.base-path:/Users/park/}")
    private String profileImageBasePath;

    @Value("${cafe.profile.image.relative-path:/img/}")
    private String profileImageRelativePath;


    private final MemberService memberService;
    private final FileService fileService;


    @Override
    @Transactional
    public void update(MemberUpdateDto updateDto, AccessTokenDto accessTokenDto) {
        Member member = memberService.getMemberByToken(accessTokenDto.getAccessToken());
        updateMemberProfile(member, updateDto);
        log.info("멤버 정보 수정 성공: {}", updateDto.getNickName());
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileDto getProfileByToken(AccessTokenDto accessTokenDto) {
        Member member = memberService.getMemberByToken(accessTokenDto.getAccessToken());
        String relativePath = convertToRelativePath(member.getProfileImagePath());
        return ProfileDto.builder()
                .email(member.getEmail())
                .nickName(member.getNickName())
                .memberRole(member.getMemberRole())
                .provider(member.getProvider())
                .profileImagePath(relativePath)
                .build();
    }

    @Override
    public MemberProfileDto getUserInfoByToken(AccessTokenDto accessTokenDto) {
        Member member = memberService.getMemberByToken(accessTokenDto.getAccessToken());
        String relativePath = convertToRelativePath(member.getProfileImagePath());
        return MemberProfileDto.builder()
                .nickName(member.getNickName())
                .profileImagePath(relativePath)
                .build();
    }


    private void updateMemberProfile(Member member, MemberUpdateDto updateDto) {
        if (updateDto.getNewProfileImage() != null && !updateDto.getNewProfileImage().isEmpty()) {
            fileService.deleteProfileImage(member.getProfileImagePath());
            String newImagePath = fileService.saveProfileImage(updateDto.getNewProfileImage());
            member.updateProfile(updateDto.getNickName(), newImagePath);
        } else {
            member.updateProfile(updateDto.getNickName(), member.getProfileImagePath());
        }
    }

    private String convertToRelativePath(String path) {
        if (path != null && path.startsWith(profileImageBasePath)) {
            return profileImageRelativePath + path.substring(path.lastIndexOf("/") + 1);
        }
        return path;
    }

}
