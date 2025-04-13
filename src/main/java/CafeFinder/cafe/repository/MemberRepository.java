package CafeFinder.cafe.repository;

import CafeFinder.cafe.domain.Member;
import CafeFinder.cafe.infrastructure.jwt.MemberAuthProjection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<MemberAuthProjection> findMemberAuthByEmail(String email);

}
