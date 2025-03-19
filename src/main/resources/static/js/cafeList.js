import {checkLoginStatus, logout} from '/js/auth.js';
import {displaySearchResults, renderPagination, searchCafes} from '/js/cafe.js';

// logout()을 인라인 이벤트 핸들러에서 사용할 수 있도록 전역에 등록
window.logout = logout;

// 현재 검색어를 저장할 변수
let currentSearchValue = "";

document.addEventListener("DOMContentLoaded", () => {
    checkLoginStatus();
    loadCafes();

    // 검색폼 제출 이벤트 리스너
    document.getElementById("cafe-search-form").addEventListener("submit", (event) => {
        event.preventDefault();
        const searchValue = document.getElementById("cafe-search-input").value.trim();
        if (!searchValue) {
            alert("검색어를 입력해주세요.");
            return;
        }
        currentSearchValue = searchValue;
        searchCafes(searchValue, 0);
    });

    // 이벤트 위임: 카페 카드 클릭 시 상세 페이지로 이동
    const container = document.getElementById("gu-review-container");
    container.addEventListener("click", (event) => {
        const card = event.target.closest(".cafe-item");
        if (card) {
            event.preventDefault();
            const cafeCode = card.getAttribute("data-cafe-code");
            if (cafeCode) {
                // POST 요청으로 클릭 이벤트 전송 후 상세 페이지로 이동
                fetch(`/api/cafes/click/${cafeCode}`, {
                    method: "POST",
                    credentials: "include"
                }).finally(() => {
                    window.location.href = `/cafe/detail/${cafeCode}`;
                });
            }
        }
    });
});

// district/테마에 따른 초기 카페 로드
function loadCafes() {
    const pathParts = window.location.pathname.split('/');
    const district = pathParts[2] ? decodeURIComponent(pathParts[2]) : '';
    const theme = pathParts[3] ? decodeURIComponent(pathParts[3]) : '';

    if (!district || !theme) {
        console.error('URL에서 district 혹은 theme 정보를 찾을 수 없습니다.');
        return;
    }

    // 쿼리 스트링에서 페이지 번호 추출 (기본값 0)
    const params = new URLSearchParams(window.location.search);
    const page = params.get('page') ? parseInt(params.get('page')) : 0;
    const url = `/api/cafes/district/${encodeURIComponent(district)}/${encodeURIComponent(theme)}?page=${page}`;

    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error("네트워크 응답에 문제가 있습니다.");
            return response.json();
        })
        .then(data => {
            displaySearchResults(data.data);
            renderPagination(data.data.totalPages, data.data.number);
        })
        .catch(error => console.error("Error fetching cafe data:", error));
}
