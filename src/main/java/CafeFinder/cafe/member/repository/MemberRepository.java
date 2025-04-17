package CafeFinder.cafe.member.repository;

import CafeFinder.cafe.member.domain.Member;
import CafeFinder.cafe.member.jwt.MemberAuthProjection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<MemberAuthProjection> findMemberAuthByEmail(String email);

}
