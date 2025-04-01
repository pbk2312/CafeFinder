export let currentSearchValue = "";

export function searchCafes(keyword, page) {
    currentSearchValue = keyword;
    const url = `/api/cafes/search?keyword=${encodeURIComponent(keyword)}&page=${page}`;
    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error("네트워크 응답 문제");
            return response.json();
        })
        .then(data => {
            displaySearchResults(data.data);
            renderPagination(data.data.totalPages, data.data.number, "search");
        })
        .catch(error => {
            console.error("검색 오류:", error);
            alert("검색 결과를 불러오는 중 오류가 발생했습니다.");
        });
}

export function displaySearchResults(pageData) {
    // 기존 구역 숨김
    const guContainer = document.getElementById("gu-review-container");
    if (guContainer) {
        guContainer.style.display = "none";
    }
    const reviewHeader = document.getElementById("review-header");
    if (reviewHeader) {
        reviewHeader.style.display = "none";
    }
    // 검색 결과 영역 보이기
    const searchHeader = document.getElementById("search-header");
    if (searchHeader) {
        searchHeader.style.display = "block";
    }
    const container = document.getElementById("search-results-container");
    container.style.display = "flex";
    container.innerHTML = "";

    pageData.content.forEach(cafe => {
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
                        <span class="badge bg-info text-dark">
                            <i class="fa-solid fa-comment me-1"></i>${cafe.reviewCount}
                        </span>
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

export function getStarRating(rating) {
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

export const themeStyles = {
    COZY: 'background-color: #FAD7A0; color: #6F4E37;',
    QUIET: 'background-color: #D5F5E3; color: #27AE60;',
    STUDY_FRIENDLY: 'background-color: #AED6F1; color: #2E86C1;',
    DESSERT: 'background-color: #F9E79F; color: #B7950B;',
    SPECIALTY_COFFEE: 'background-color: #F5B7B1; color: #C0392B;',
    NONE: 'background-color: #D7DBDD; color: #7B7D7D;'
};

export const themeDescriptions = {
    COZY: '분위기 좋은',
    QUIET: '조용한',
    STUDY_FRIENDLY: '공부하기 좋은',
    DESSERT: '디저트 맛집',
    SPECIALTY_COFFEE: '커피 맛집',
    NONE: '기타'
};

export function renderPagination(totalPages, currentPage, mode = "district") {
    const paginationContainer = document.getElementById("pagination-container");
    paginationContainer.innerHTML = "";

    const ul = document.createElement("ul");
    ul.className = "pagination justify-content-center";

    // 이전 페이지 버튼
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
    ul.appendChild(prevItem);

    // 페이지 번호 버튼
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
        ul.appendChild(pageItem);
    }

    // 다음 페이지 버튼
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
    ul.appendChild(nextItem);

    paginationContainer.appendChild(ul);
}
