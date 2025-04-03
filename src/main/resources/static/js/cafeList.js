import {toggleScrap} from './toggleScrap.js';
import {checkLoginStatus, logout} from "./auth.js";
import {renderPagination} from "./renderPagination.js";
import {createCafeCard} from "./displaySeacrhCards.js";

window.logout = logout;
let currentSearchValue = "";

document.addEventListener("DOMContentLoaded", function () {

    loadCafes(); // 카페들 가져오기

    checkLoginStatus().then(isLoggedIn => {
        console.log("현재 로그인 상태:", isLoggedIn);
    });


    document.getElementById("cafe-search-form").addEventListener("submit", function (event) {
        event.preventDefault();
        const searchValue = document.getElementById("cafe-search-input").value.trim();
        if (!searchValue) {
            alert("검색어를 입력해주세요.");
            return;
        }
        currentSearchValue = searchValue;
        searchCafes(searchValue, 0);
    });
});


function loadCafes() {
    const pathParts = window.location.pathname.split('/');
    const district = pathParts[2] ? decodeURIComponent(pathParts[2]) : '';
    const theme = pathParts[3] ? decodeURIComponent(pathParts[3]) : '';

    if (!district || !theme) {
        console.error('URL에서 district 혹은 theme 정보를 찾을 수 없습니다.');
        return;
    }

    const params = new URLSearchParams(window.location.search);
    const page = params.get('page') ? parseInt(params.get('page')) : 0;
    const url = `/api/cafes/district/${encodeURIComponent(district)}/${encodeURIComponent(theme)}?page=${page}`;

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error("네트워크 응답에 문제가 있습니다.");
            }
            return response.json();
        })
        .then(data => {
            console.log("카페 데이터 (loadCafes):", data); // 디버그 로그: 전체 응답 확인
            displaySearchResults(data.data);
            renderPagination(data.data.totalPages, data.data.number);
        })
        .catch(error => console.error("Error fetching cafe data:", error));
}

function searchCafes(keyword, page) {
    const url = `/api/cafes/search?keyword=${encodeURIComponent(keyword)}&page=${page}`;
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error("네트워크 응답에 문제가 있습니다.");
            }
            return response.json();
        })
        .then(data => {
            console.log("검색 카페 데이터 (searchCafes):", data); // 디버그 로그: 전체 응답 확인
            displaySearchResults(data.data);
            // 검색 결과용 페이지네이션 렌더링 (mode 파라미터 "search" 전달)
            renderPagination(data.data.totalPages, data.data.number, "search", currentSearchValue);
        })
        .catch(error => console.error("검색 결과를 불러오는 중 오류:", error));
}


function displaySearchResults(pageData) {
    const container = document.getElementById("gu-review-container");
    container.innerHTML = "";
    pageData.content.forEach(cafe => {
        const cafeCard = createCafeCard(cafe);
        container.appendChild(cafeCard);
    });

    // 스크랩 버튼 이벤트 핸들러 등록
    document.querySelectorAll('.btn-scrap').forEach(button => {
        button.addEventListener('click', (e) => {
            e.stopPropagation(); // 카드 클릭 이벤트와 충돌 방지
            const cafeCode = button.getAttribute('data-cafe-code');
            toggleScrap(cafeCode, button);
        });
    });
}


// 카페 카드 클릭 시 상세 페이지로 이동
document.getElementById("gu-review-container").addEventListener("click", (event) => {
    const card = event.target.closest(".cafe-item");
    if (card) {
        const cafeCode = card.getAttribute("data-cafe-code");
        if (cafeCode) {
            window.location.href = `/cafe/detail/${cafeCode}`;
        }
    }
});
