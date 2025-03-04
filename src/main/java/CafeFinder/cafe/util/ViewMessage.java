package CafeFinder.cafe.util;

import lombok.Getter;

@Getter
public enum ViewMessage {

    SIGN_UP_SUCCESS("회원가입 성공"),
    LOGIN_SUCCESS("로그인 성공"),
    LOGOUT_SUCCESS("로그아웃 성공"),
    NOT_LOGIN("비로그인 상태"),
    EMAIL_SEND_SUCCESS("인증번호 이메일 전송 성공"),
    EMAIL_VERIFY_SUCCESS("이메일 인증 성공"),
    UPDATE_PROFILE("프로필 수정 성공"),
    PROFILE_INFO("프로필 정보 조회 성공"),
    GUREVIEW_STATS_SUCCESS("구 별 리뷰 통계 데이터 조회 성공"),
    DISTRCT_THEME_GET_SUCCESS("구 , 테마별 조회 성공"),
    GET_CAFE_THEME("카페 테마 조회"),
    GENERATE_ACCESSTOKEN("액세스 토큰 만료됨, 새로운 액세스 토큰 발급 완료");
    private final String message;

    ViewMessage(String message) {
        this.message = message;
    }

}
