import {toggleScrap} from './toggleScrap.js';
import {themeDescriptions, themeStyles} from './theme.js';
import {getStarRating} from "./star.js";
import {checkLoginStatus, logout} from "./auth.js";


window.logout = logout;
let currentSearchValue = "";

document.addEventListener("DOMContentLoaded", function () {
    loadCafes();

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
            renderPagination(data.data.totalPages, data.data.number, "search");
        })
        .catch(error => console.error("검색 결과를 불러오는 중 오류:", error));
}


function displaySearchResults(pageData) {
    const container = document.getElementById("gu-review-container");
    container.innerHTML = "";
    pageData.content.forEach(cafe => {
        console.log("카페 정보:", cafe);
        const col = document.createElement("div");
        col.className = "col-md-4 mb-4 cafe-item";
        col.setAttribute("data-cafe-code", cafe.cafeCode);
        col.innerHTML = `
      <div class="card h-100 cafe-card">
        <img src="${cafe.imageUrl ? cafe.imageUrl : '/default-cafe.png'}" class="card-img-top" alt="${cafe.name}">
        <div class="card-body" style="position: relative;">
          <h5 class="card-title">${cafe.name}</h5>
          <p class="card-text">${cafe.address}</p>
          ${cafe.openingHours ? `<p class="card-text"><small class="text-muted">${cafe.openingHours}</small></p>` : ''}
          ${cafe.phoneNumber ? `<p class="card-text"><small class="text-muted">전화번호: ${cafe.phoneNumber}</small></p>` : ''}
          <p class="card-text">
            <small class="text-muted">
              평점: <span class="star-rating">${getStarRating(cafe.averageRating)}</span>
            </small>
          </p>
          <p class="card-text">
            ${
            cafe.themes && Array.isArray(cafe.themes)
                ? cafe.themes.map(t => `<span class="badge theme-badge" style="${themeStyles[t] || 'background-color: #ccc; color: #fff;'}">${themeDescriptions[t] || t}</span>`).join('')
                : '<span class="badge theme-badge" style="background-color: #ccc; color: #fff;">기타</span>'
        }
          </p>
          <!-- 스크랩 버튼을 오른쪽 하단에, 크기는 btn-sm으로 줄임 -->
              <div class="d-flex justify-content-end align-items-center">
<p class="card-text mb-0 me-2 review-count">
  <span class="badge">
    <i class="fa-solid fa-comment me-1"></i>${cafe.reviewCount}
  </span>
</p>

  <button class="btn btn-outline-primary btn-sm btn-scrap" data-cafe-code="${cafe.cafeCode}">
    <i class="fa-regular fa-heart"></i> 스크랩
  </button>
</div>
       
    `;
        container.appendChild(col);
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

function renderPagination(totalPages, currentPage, mode = "district") {
    const paginationContainer = document.getElementById("pagination-container");
    paginationContainer.innerHTML = "";


    const prevItem = document.createElement("li");
    prevItem.className = "page-item" + (currentPage <= 0 ? " disabled" : "");
    const prevLink = document.createElement("a");
    prevLink.className = "page-link";
    prevLink.href = "#";
    prevLink.innerHTML = "&laquo;";
    prevLink.addEventListener("click", function (e) {
        e.preventDefault();
        if (currentPage > 0) {
            if (mode === "search") {
                searchCafes(currentSearchValue, currentPage - 1);
            } else {
                window.location.href = `?page=${currentPage - 1}`;
            }
        }
    });
    prevItem.appendChild(prevLink);
    paginationContainer.appendChild(prevItem);


    const startPage = Math.floor(currentPage / 10) * 10;
    let endPage = startPage + 9;
    if (endPage >= totalPages) {
        endPage = totalPages - 1;
    }
    for (let i = startPage; i <= endPage; i++) {
        const pageItem = document.createElement("li");
        pageItem.className = "page-item" + (i === currentPage ? " active" : "");
        const pageLink = document.createElement("a");
        pageLink.className = "page-link";
        pageLink.href = "#";
        pageLink.innerText = i + 1;
        pageLink.addEventListener("click", function (e) {
            e.preventDefault();
            if (mode === "search") {
                searchCafes(currentSearchValue, i);
            } else {
                window.location.href = `?page=${i}`;
            }
        });
        pageItem.appendChild(pageLink);
        paginationContainer.appendChild(pageItem);
    }

    // 다음 버튼
    const nextItem = document.createElement("li");
    nextItem.className = "page-item" + (currentPage >= totalPages - 1 ? " disabled" : "");
    const nextLink = document.createElement("a");
    nextLink.className = "page-link";
    nextLink.href = "#";
    nextLink.innerHTML = "&raquo;";
    nextLink.addEventListener("click", function (e) {
        e.preventDefault();
        if (currentPage < totalPages - 1) {
            if (mode === "search") {
                searchCafes(currentSearchValue, currentPage + 1);
            } else {
                window.location.href = `?page=${currentPage + 1}`;
            }
        }
    });
    nextItem.appendChild(nextLink);
    paginationContainer.appendChild(nextItem);
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
