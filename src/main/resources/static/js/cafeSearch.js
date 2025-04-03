import {renderPagination} from "./renderPagination.js";
import {createCafeCard} from "./displaySeacrhCards.js";
import {toggleScrap} from "./toggleScrap.js";

export let currentSearchValue = "";

export function searchCafes(keyword, page) {
    currentSearchValue = keyword;
    const url = `/api/cafes/search?keyword=${encodeURIComponent(keyword)}&page=${page}`;
    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error("네트워크 응답 문제");
            return response.json();
        })
        .then(data => { // 성공
            // 검색 카페들 보여주기
            displaySearchResults(data.data);
            // 페이지 처리
            renderPagination(data.data.totalPages, data.data.number, "search", searchCafes, currentSearchValue);
        })
        .catch(error => {
            console.error("검색 오류:", error);
            alert("검색 결과를 불러오는 중 오류가 발생했습니다.");
        });
}


export function displaySearchResults(pageData) {

    // 숨김
    const guContainer = document.getElementById("gu-review-container");
    if (guContainer) {
        guContainer.style.display = "none";
    }

    // 숨김
    const reviewHeader = document.getElementById("review-header");
    if (reviewHeader) {
        reviewHeader.style.display = "none";
    }

    // 검색 결과 영역 보이기
    const searchHeader = document.getElementById("search-header");
    if (searchHeader) {
        searchHeader.style.display = "block";
    }
    // 검색 결과 컨테이너 보이게 하기
    const container = document.getElementById("search-results-container");
    container.style.display = "flex";
    container.innerHTML = "";

    pageData.content.forEach(cafe => {
        const cafeCard = createCafeCard(cafe);
        container.appendChild(cafeCard);
    });

    // 스크랩 버튼 이벤트 핸들러 등록
    document.querySelectorAll('.btn-scrap').forEach(button => {
        button.addEventListener('click', (e) => {
            e.stopPropagation();
            const cafeCode = button.getAttribute('data-cafe-code');
            toggleScrap(cafeCode, button);
        });
    });

}
