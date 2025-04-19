package CafeFinder.cafe.member.security.jwt;

import CafeFinder.cafe.member.domain.MemberRole;

public interface MemberAuthProjection {

    Long getId();

    String getNickName();

    String getEmail();

    MemberRole getMemberRole();

    String getPassword();

}
