package CafeFinder.cafe.cafe.service;

import CafeFinder.cafe.cafe.domain.Cafe;
import CafeFinder.cafe.cafe.domain.CafeScrap;
import CafeFinder.cafe.cafe.dto.CafeDto;
import CafeFinder.cafe.cafe.dto.CafeScrapDto;
import CafeFinder.cafe.cafe.dto.ScrapCafeCodeDto;
import CafeFinder.cafe.cafe.repository.CafeRepository;
import CafeFinder.cafe.cafe.repository.CafeScrapRepository;
import CafeFinder.cafe.global.infrastructure.elasticSearch.CafeSearchRepository;
import CafeFinder.cafe.global.infrastructure.elasticSearch.IndexedCafe;
import CafeFinder.cafe.global.infrastructure.redis.CafeScrapsRedisService;
import CafeFinder.cafe.member.domain.Member;
import CafeFinder.cafe.member.dto.AccessTokenDto;
import CafeFinder.cafe.member.exception.UnauthorizedException;
import CafeFinder.cafe.member.service.MemberService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@RequiredArgsConstructor
@Slf4j
@Service
public class CafeScrapServiceImpl implements CafeScrapService {

    private final MemberService memberService;
    private final CafeScrapsRedisService scrapsRedisService;
    private final CafeSearchRepository cafeSearchRepository;
    private final CafeScrapRepository cafeScrapRepository;
    private final CafeRepository cafeRepository;

    @Override
    @Transactional
    public boolean cafeScraps(CafeScrapDto dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberService.getMemberByEmail(email);

        Cafe cafe = cafeRepository.getReferenceById(dto.getCafeCode());

        CafeScrap scrap = CafeScrap.builder()
                .member(member)
                .cafe(cafe)
                .build();
        cafeScrapRepository.save(scrap);

        // 4) 커밋 후에 Redis 동기화
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        boolean ok = scrapsRedisService.toggleCafeScrap(member.getId(), dto.getCafeCode());
                        if (!ok) {
                            log.error("Redis 업데이트 실패: memberId={}, cafeCode={}", member.getId(), dto.getCafeCode());
                        }
                    }
                }
        );
        return true;
    }


    @Override
    public List<CafeDto> getCafeScraps(AccessTokenDto accessTokenDto) {
        Member member = getMemberByToken(accessTokenDto.getAccessToken());
        List<String> cafeCodes = scrapsRedisService.getCafeCodesForMember(member.getId());
        List<IndexedCafe> indexedCafes = cafeCodes.stream()
                .map(cafeSearchRepository::findByCafeCode)
                .flatMap(Optional::stream)
                .toList();
        return indexedCafes.stream().map(CafeDto::fromDocumentForList)
                .toList();
    }

    @Override
    public List<ScrapCafeCodeDto> getCafeScrapCodes(AccessTokenDto accessTokenDto) {
        Member member = getMemberByToken(accessTokenDto.getAccessToken());
        List<String> cafeCodes = scrapsRedisService.getCafeCodesForMember(member.getId());
        return cafeCodes.stream()
                .map(ScrapCafeCodeDto::from)
                .toList();
    }

    private Member getMemberByToken(String accessToken) {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new UnauthorizedException();
        }
        return memberService.getMemberByToken(accessToken);
    }

}
