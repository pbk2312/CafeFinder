package CafeFinder.cafe.member.service;

import static CafeFinder.cafe.global.exception.ErrorCode.MEMBER_NOT_FOUND;

import CafeFinder.cafe.global.exception.ErrorException;
import CafeFinder.cafe.member.domain.Member;
import CafeFinder.cafe.member.dto.MemberProfileDto;
import CafeFinder.cafe.member.dto.ProfileDto;
import CafeFinder.cafe.member.repository.MemberRepository;
import CafeFinder.cafe.member.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public ProfileDto getProfileByToken() {
        Member member = getMember();
        // S3 URL 그대로 사용
        return ProfileDto.builder()
            .email(member.getEmail())
            .nickName(member.getNickName())
            .memberRole(member.getMemberRole())
            .provider(member.getProvider())
            .profileImagePath(member.getProfileImagePath())
            .build();
    }

    @Override
    public MemberProfileDto getMemberInfo() {
        Member member = getMember();
        // S3 URL 그대로 사용
        return MemberProfileDto.builder()
            .nickName(member.getNickName())
            .profileImagePath(member.getProfileImagePath())
            .build();
    }

    private Member getMember() {
        Long memberId = SecurityUtil.getMemberId();
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new ErrorException(MEMBER_NOT_FOUND));
    }
}
