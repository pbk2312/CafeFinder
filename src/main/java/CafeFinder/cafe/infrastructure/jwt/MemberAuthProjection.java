package CafeFinder.cafe.infrastructure.jwt;

import CafeFinder.cafe.domain.MemberRole;

public interface MemberAuthProjection {

    String getEmail();

    String getPassword();

    MemberRole getMemberRole();

}
