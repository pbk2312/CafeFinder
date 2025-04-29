package CafeFinder.cafe.member.service;

import static CafeFinder.cafe.global.exception.ErrorCode.MEMBER_NOT_FOUND;

import CafeFinder.cafe.global.exception.ErrorException;
import CafeFinder.cafe.global.service.FileService;
import CafeFinder.cafe.member.domain.Member;
import CafeFinder.cafe.member.dto.MemberProfileDto;
import CafeFinder.cafe.member.dto.MemberUpdateDto;
import CafeFinder.cafe.member.dto.ProfileDto;
import CafeFinder.cafe.member.repository.MemberRepository;
import CafeFinder.cafe.member.security.util.SecurityUtil;
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
    private final MemberRepository memberRepository;


    @Override
    @Transactional
    public void update(MemberUpdateDto updateDto) {

        Member member = getMember();

        updateMemberProfile(member, updateDto);
        log.info("멤버 정보 수정 성공: {}", updateDto.getNickName());
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileDto getProfileByToken() {
        Member member = getMember();
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
    public MemberProfileDto getMemberInfo() {
        Member member = getMember();
        String relativePath = convertToRelativePath(member.getProfileImagePath());
        return MemberProfileDto.builder()
            .nickName(member.getNickName())
            .profileImagePath(relativePath)
            .build();
    }

    private Member getMember() {
        Long memberId = SecurityUtil.getMemberId();
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new ErrorException(MEMBER_NOT_FOUND));
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
