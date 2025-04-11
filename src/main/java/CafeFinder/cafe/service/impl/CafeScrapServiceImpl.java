package CafeFinder.cafe.service.impl;

import CafeFinder.cafe.domain.Cafe;
import CafeFinder.cafe.domain.CafeScrap;
import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.dto.AccessTokenDto;
import CafeFinder.cafe.dto.CafeDto;
import CafeFinder.cafe.dto.CafeScrapDto;
import CafeFinder.cafe.dto.ScrapCafeCodeDto;
import CafeFinder.cafe.exception.UnauthorizedException;
import CafeFinder.cafe.infrastructure.elasticSearch.CafeSearchRepository;
import CafeFinder.cafe.infrastructure.elasticSearch.IndexedCafe;
import CafeFinder.cafe.infrastructure.redis.CafeScrapsRedisService;
import CafeFinder.cafe.repository.CafeScrapRepository;
import CafeFinder.cafe.service.interfaces.CafeScrapService;
import CafeFinder.cafe.service.interfaces.CafeService;
import CafeFinder.cafe.service.interfaces.MemberService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final CafeService cafeService;

    @Override
    @Transactional
    public boolean cafeScraps(CafeScrapDto cafeScrapDto) {
        Member member = getMemberByToken(cafeScrapDto.getAccessToken());

        CafeDto cafeDto = cafeService.getCafe(cafeScrapDto.getCafeCode());
        Cafe cafe = cafeDto.toEntity();

        CafeScrap cafeScrap = CafeScrap.builder()
                .member(member)
                .cafe(cafe)
                .build();

        cafeScrapRepository.save(cafeScrap);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                boolean redisResult = scrapsRedisService.toggleCafeScrap(member.getId(), cafeScrapDto.getCafeCode());
                if (!redisResult) {
                    log.error("Redis 업데이트 실패: memberId={}, cafeCode={}", member.getId(), cafeScrapDto.getCafeCode());
                }
            }
        });
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
