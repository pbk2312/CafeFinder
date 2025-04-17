package CafeFinder.cafe.member.jwt;

import CafeFinder.cafe.member.domain.MemberRole;

public interface MemberAuthProjection {

    String getEmail();

    String getPassword();

    MemberRole getMemberRole();

}
