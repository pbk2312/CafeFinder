// 전역 변수: 현재 검색어 저장
let currentSearchValue = "";

document.addEventListener("DOMContentLoaded", function () {
    checkLoginStatus();
    loadCafes(); // URL에 district/테마가 있을 경우 로드

    // 검색폼 제출 이벤트 리스너 추가
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

function checkLoginStatus() {
    fetch("/api/member/validateToken", {
        method: "GET",
        credentials: "include"
    })
        .then(response => response.json())
        .then(data => {
            console.log("로그인 상태:", data); // 디버그 로그
            if (data.success && data.data) {
                updateNavbarForLoggedInUser(data.data.nickName, data.data.profileImagePath);
            }
        })
        .catch(error => console.error("로그인 상태 확인 오류:", error));
}

function updateNavbarForLoggedInUser(nickName, profileImagePath) {
    const navbar = document.getElementById("navbarNav");
    const loginLink = document.getElementById("login-link");
    if (loginLink) loginLink.remove();

    const profileImageTag = profileImagePath
        ? `<img src="${profileImagePath}" class="rounded-circle" width="40" height="40" alt="프로필 이미지">`
        : `<img src="/default-profile.png" class="rounded-circle" width="40" height="40" alt="기본 프로필">`;

    navbar.innerHTML += `
    <li class="nav-item d-flex align-items-center me-3">
      <a class="nav-link" href="#" onclick="logout()">로그아웃</a>
    </li>
    <li class="nav-item d-flex align-items-center">
      <a class="nav-link d-flex align-items-center" href="/member/profile">
        ${profileImageTag} <span class="ms-2 fw-semibold">${nickName}</span>
      </a>
    </li>
  `;
}

function logout() {
    fetch("/api/member/logout", {
        method: "POST",
        credentials: "include"
    })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                console.error("로그아웃 실패");
            }
        })
        .catch(error => console.error("로그아웃 요청 오류:", error));
}

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
        console.log("카페 정보:", cafe); // 각 카페의 데이터 확인 (전화번호, 평점 등)
        const col = document.createElement("div");
        col.className = "col-md-4 mb-4 cafe-item";
        col.setAttribute("data-cafe-code", cafe.cafeCode);
        col.innerHTML = `
      <div class="card h-100 cafe-card">
        <img src="${cafe.imageUrl ? cafe.imageUrl : '/default-cafe.png'}" class="card-img-top" alt="${cafe.name}">
        <div class="card-body">
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
        </div>
      </div>
    `;
        container.appendChild(col);
    });
}

function getStarRating(rating) {
    const fullStars = Math.floor(rating);
    const halfStar = (rating - fullStars) >= 0.5 ? 1 : 0;
    const emptyStars = 5 - fullStars - halfStar;
    let starsHtml = '';
    for (let i = 0; i < fullStars; i++) {
        starsHtml += '<i class="fa-solid fa-star" style="color: gold;"></i>';
    }
    if (halfStar) {
        starsHtml += '<i class="fa-solid fa-star-half-alt" style="color: gold;"></i>';
    }
    for (let i = 0; i < emptyStars; i++) {
        starsHtml += '<i class="fa-regular fa-star" style="color: gold;"></i>';
    }
    return starsHtml;
}

const themeStyles = {
    COZY: 'background-color: #FAD7A0; color: #6F4E37;',
    QUIET: 'background-color: #D5F5E3; color: #27AE60;',
    STUDY_FRIENDLY: 'background-color: #AED6F1; color: #2E86C1;',
    DESSERT: 'background-color: #F9E79F; color: #B7950B;',
    SPECIALTY_COFFEE: 'background-color: #F5B7B1; color: #C0392B;',
    NONE: 'background-color: #D7DBDD; color: #7B7D7D;'
};

const themeDescriptions = {
    COZY: '분위기 좋은',
    QUIET: '조용한',
    STUDY_FRIENDLY: '공부하기 좋은',
    DESSERT: '디저트 맛집',
    SPECIALTY_COFFEE: '커피 맛집',
    NONE: '기타'
};

// 페이지네이션 렌더링 함수 (mode: "search"일 경우 검색 결과에 맞게 처리)
function renderPagination(totalPages, currentPage, mode = "district") {
    const paginationContainer = document.getElementById("pagination-container");
    paginationContainer.innerHTML = "";

    // 이전 버튼
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

    // 10페이지씩 묶어서 페이지 번호 버튼 생성
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
