package CafeFinder.cafe.global.exception;

import static CafeFinder.cafe.global.exception.ErrorStatus.BAD_REQUEST;
import static CafeFinder.cafe.global.exception.ErrorStatus.CONFLICT;
import static CafeFinder.cafe.global.exception.ErrorStatus.INTERNAL_SERVER_ERROR;
import static CafeFinder.cafe.global.exception.ErrorStatus.NOT_FOUND;
import static CafeFinder.cafe.global.exception.ErrorStatus.UNAUTHORIZED;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    NOT_FOUND_CAFE(NOT_FOUND, "CAFE-001", "존재하지 않는 카페입니다."),
    INVALID_LOCATION_FORMAT(BAD_REQUEST, "CAFE-002", "잘못된 위도, 경도 형식입니다."),
    INVALID_DISTRICT_THEME(BAD_REQUEST, "CAFE-003", "잘못된 district 또는 theme 값입니다."),
    INVALID_CAFE_SEARCH(BAD_REQUEST, "CAFE-004", "잘못된 검색어 입니다."),

    PASSWORD_INCORRECT(BAD_REQUEST, "AUTH-001", "비밀번호가 일치하지 않습니다."),
    INVALID_JWT(UNAUTHORIZED, "AUTH-002", "유효하지 않은 JWT 토큰입니다."),
    MEMBER_ALREADY_EXISTS(CONFLICT, "AUTH-003", "해당 이메일은 이미 존재합니다."),
    MEMBER_NOT_FOUND(NOT_FOUND, "AUTH-004", "회원이 존재하지 않습니다."),
    INVALID_PASSWORD_MATCH(BAD_REQUEST, "AUTH-005", "두 비밀번호가 일치하지 않습니다."),
    LOGIN_REQUIRED(UNAUTHORIZED, "AUTH-006", "로그인이 필요한 서비스입니다."),
    LEVEL_ACCESS_DENIED(UNAUTHORIZED, "AUTH-007", "고객님의 등급은 접근할 수 없습니다."),
    VERIFY_CODE_MISMATCH(BAD_REQUEST, "AUTH-008", "인증번호가 일치하지 않습니다."),
    NOT_VERIFIED_EMAIL(BAD_REQUEST, "AUTH-009", "이메일 인증이 완료되지 않았습니다."),
    UNSUPPORTED_PROVIDER(BAD_REQUEST, "AUTH-010", "소셜로그인 오류(제공자 오류)"),
    PROFILE_SAVE_EXCEPTION(INTERNAL_SERVER_ERROR, "AUTH-011", "프로필 이미지 저장 중 오류 발생"),
    EXPIRED_TOKEN(UNAUTHORIZED, "AUTH-012", "만료된 토큰입니다."),
    DUPLICATED_SOCIAL(CONFLICT, "AUTH-013", "이미 다른 이메일로 가입된 유저입니다. 다시 로그인 부탁드립니다."),
    OAUTH_PROVIDER_MISMATCH(CONFLICT, "AUTH-014", "가입된 소셜 제공자와 일치하지 않습니다. 다른 계정으로 로그인 해주세요."),

    SERVER_ERROR(INTERNAL_SERVER_ERROR, "COMMON-001", "서버 내부 오류"),
    VALIDATION_FAILED(BAD_REQUEST, "COMMON-002", "유효성 검증 실패"),
    REDIS_ERROR(INTERNAL_SERVER_ERROR, "COMMON-003", "Redis 작업 중 오류가 발생했습니다."),

    ;

    private final ErrorStatus errorStatus;
    private final String code;
    private final String message;
}
