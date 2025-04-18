package CafeFinder.cafe.global.util;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    MEMBER_NOT_FOUND("회원이 존재하지 않습니다."),
    PASSWORD_CONFIRMATION_MISMATCH("두 비밀번호가 일치하지 않습니다."),
    PASSWORD_INCORRECT("비밀번호가 일치하지 않습니다."),
    SERVER_ERROR("서버 내부오류"),
    VALIDATION_FAILED("유효성 검증 실패"),
    LOGIN_ERROR_MESSAGE("로그인이 필요한 서비스입니다."),
    LEVEL_MESSAGE("고객님의 등급은 접근할수없습니다."),
    Member_AlreadyExists("해당 이메일은 이미 존재합니다."),
    VERIFY_CODE_MIS_MATCH("인증번호가 일치하지 않습니다."),
    NOT_VERIFY_CODE("이메일 인증이 완료 되지 않았습니다."),
    UNSUPPORTEDPROVIDER("소셜로그인 오류(제공자 오류) : "),
    PROFILESAVEEXCEPTION("프로필 이미지 저장 중 오류 발생"),
    WRONG_DISTRCIT_THEME("잘못된 district 또는 theme 값입니다."),
    CAFE_INFO_NOT_FOUND("해당 카페 코드가 존재하지 않습니다 : {} "),
    WRONG_SEARCH("잘못된 검색어 입니다."),
    UNAUTHORIZED("인증되지않은 사용자입니다."),
    INVALID_LOCATION_FORMAT("잘못된 위도, 경도 형식입니다."),
    REDIS_ERROR("Redis 작업 중 오류가 발생했습니다. (memberId=%s, cafeCode=%s)"),
    EXPRIED_TOKEN("만료된 토큰입니다"),
    DUPLICARTED_SOCAIL("이미 다른 이메일로 가입 되어있는 유저입니다. 다시 로그인 부탁드립니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

}
