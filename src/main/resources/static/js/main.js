import {checkLoginStatus, logout} from "./auth.js";
import {getStarRating, searchCafes, themeDescriptions, themeStyles} from "./cafe.js";

// 로그아웃 함수를 인라인 이벤트 핸들러(예: onclick)에서 사용할 수 있도록 전역에 등록
window.logout = logout;

document.addEventListener("DOMContentLoaded", () => {
    // 로그인 여부와 상관없이 인기 카페는 항상 호출
    fetchPopularCafes();

    // 인기 카페 컨테이너 클릭 이벤트 위임 추가
    const popularContainer = document.getElementById("popular-cafe-container");
    if (popularContainer) {
        popularContainer.addEventListener("click", (event) => {
            const card = event.target.closest(".cafe-item");
            if (card) {
                const cafeCode = card.getAttribute("data-cafe-code");
                if (cafeCode) {
                    fetch(`/api/cafes/click/${cafeCode}`, {
                        method: "POST",
                        credentials: "include",
                    }).finally(() => {
                        window.location.href = `/cafe/detail/${cafeCode}`;
                    });
                }
            }
        });
    }

    // 로그인 상태 확인 후, 로그인 되어 있으면 추천 카페 데이터를 불러오고,
    // 비로그인 상태이면 추천 섹션을 숨김 처리
    checkLoginStatus().then((isLoggedIn) => {
        if (isLoggedIn) {
            fetchRecommendedCafes();
        } else {
            const recommendedSection = document.getElementById("recommended-section");
            if (recommendedSection) {
                recommendedSection.style.display = "none";
            }
        }
    });

    // 구 리뷰 통계 불러오기
    fetchGuReviewStats();

    // 검색 폼 이벤트 리스너 설정 (cafe.js의 searchCafes() 함수 사용)
    const searchForm = document.getElementById("cafe-search-form");
    if (searchForm) {
        searchForm.addEventListener("submit", (event) => {
            event.preventDefault();
            const searchInput = document.getElementById("cafe-search-input");
            if (!searchInput) return;
            const searchValue = searchInput.value.trim();
            if (!searchValue) {
                alert("검색어를 입력해주세요.");
                return;
            }
            searchCafes(searchValue, 0);
        });
    }

    // 이벤트 위임: 구 리뷰 컨테이너 내 카드 클릭 시 상세 페이지로 이동
    const guContainer = document.getElementById("gu-review-container");
    if (guContainer) {
        guContainer.addEventListener("click", (event) => {
            const card = event.target.closest(".cafe-item");
            if (card) {
                const cafeCode = card.getAttribute("data-cafe-code");
                if (cafeCode) {
                    fetch(`/api/cafes/click/${cafeCode}`, {
                        method: "POST",
                        credentials: "include",
                    }).finally(() => {
                        window.location.href = `/cafe/detail/${cafeCode}`;
                    });
                }
            }
        });
    }

    // 이벤트 위임: 추천 카페 컨테이너 내 카드 클릭 시 상세 페이지로 이동
    const recommendedContainer = document.getElementById("recommended-cafe-container");
    if (recommendedContainer) {
        recommendedContainer.addEventListener("click", (event) => {
            const card = event.target.closest(".cafe-item");
            if (card) {
                const cafeCode = card.getAttribute("data-cafe-code");
                if (cafeCode) {
                    fetch(`/api/cafes/click/${cafeCode}`, {
                        method: "POST",
                        credentials: "include",
                    }).finally(() => {
                        window.location.href = `/cafe/detail/${cafeCode}`;
                    });
                }
            }
        });
    }

    // 이벤트 위임: 검색 결과 컨테이너 내 카드 클릭 시 상세 페이지로 이동
    const searchResultsContainer = document.getElementById("search-results-container");
    if (searchResultsContainer) {
        searchResultsContainer.addEventListener("click", (event) => {
            const card = event.target.closest(".cafe-item");
            if (card) {
                const cafeCode = card.getAttribute("data-cafe-code");
                if (cafeCode) {
                    fetch(`/api/cafes/click/${cafeCode}`, {
                        method: "POST",
                        credentials: "include",
                    }).finally(() => {
                        window.location.href = `/cafe/detail/${cafeCode}`;
                    });
                }
            }
        });
    }
});

// ── 인기 카페 관련 함수 ──
function fetchPopularCafes() {
    fetch("/api/cafes/mostClicked")
        .then((response) => response.json())
        .then((data) => {
            if (data.success && Array.isArray(data.data)) {
                displayPopularCafes(data.data);
            }
        })
        .catch((error) => {
            console.error("인기 카페 불러오기 오류:", error);
        });
}

function displayPopularCafes(cafes) {
    const container = document.getElementById("popular-cafe-container");
    if (!container) return;
    container.innerHTML = "";
    cafes.forEach((cafe) => {
        const col = document.createElement("div");
        col.className = "col-md-4 mb-4 cafe-item";
        col.setAttribute("data-cafe-code", cafe.cafeCode);
        col.innerHTML = `
            <div class="card cafe-card">
                <img src="${cafe.imageUrl ? cafe.imageUrl : "/default-cafe.png"}" class="card-img-top" alt="${cafe.name}">
                <div class="card-body">
                    <h5 class="card-title">${cafe.name}</h5>
                    <p class="card-text">${cafe.address}</p>
                    <div class="rating-section">
                        <span class="star-rating">${getStarRating(cafe.averageRating)}</span>
                        <span class="rating-value">(${cafe.reviewCount} 리뷰)</span>
                    </div>
                </div>
            </div>
        `;
        container.appendChild(col);
    });
}

// ── 구 리뷰 통계 관련 함수 ──
function fetchGuReviewStats() {
    fetch("/api/cafes/guReviewStats")
        .then((response) => response.json())
        .then((data) => {
            if (data.success) {
                displayGuReviewStats(data.data);
                updateStarRatings();
            }
        })
        .catch((error) => console.error("구 리뷰 통계 가져오기 오류:", error));
}

const guMapping = {
    GN: "강남구",
    GD: "강동구",
    GB: "강북구",
    GS: "강서구",
    GA: "관악구",
    GJ: "광진구",
    GR: "구로구",
    GC: "금천구",
    NW: "노원구",
    DB: "도봉구",
    DD: "동대문구",
    DJ: "동작구",
    MP: "마포구",
    SDM: "서대문구",
    SC: "서초구",
    SD: "성동구",
    SB: "성북구",
    SP: "송파구",
    YC: "양천구",
    YD: "영등포구",
    YS: "용산구",
    EP: "은평구",
    JR: "종로구",
    JG: "중구",
    JL: "중랑구",
};

function displayGuReviewStats(statsList) {
    const container = document.getElementById("gu-review-container");
    if (!container) return;
    container.innerHTML = "";
    statsList.forEach((stat) => {
        const fullName = guMapping[stat.guCode] || stat.guCode;
        const cardHtml = `
      <div class="col-xl-3 col-lg-4 col-md-6 col-sm-12 mb-4 cafe-item" data-cafe-code="${stat.guCode}">
        <div class="card cafe-card">
          <div class="card-header">
            <h5 class="card-title mb-0">${fullName}</h5>
          </div>
          <div class="card-body">
            <div class="rating-section">
              <div class="star-rating" data-rating="${stat.averageRating.toFixed(1)}">★★★★★</div>
              <span class="rating-value">${stat.averageRating.toFixed(1)}</span>
            </div>
            <p class="card-text reviews-text">📝 후기 <strong>${stat.totalReviews}</strong></p>
            <a href="/cafe/${stat.guCode}" class="btn btn-primary">탐험 하기</a>
          </div>
        </div>
      </div>
    `;
        container.innerHTML += cardHtml;
    });
}

function updateStarRatings() {
    document.querySelectorAll(".star-rating").forEach((starContainer) => {
        const rating = parseFloat(starContainer.getAttribute("data-rating"));
        let stars = "";
        for (let i = 1; i <= 5; i++) {
            stars += i <= rating ? "★" : "☆";
        }
        starContainer.innerHTML = stars;
    });
}

// ── 추천 카페 관련 함수 ──
function fetchRecommendedCafes() {
    fetch("/api/member/getRecommandCafes", {credentials: "include"})
        .then((response) => response.json())
        .then((data) => {
            if (data.success && Array.isArray(data.data)) {
                displayRecommendedCafes(data.data);
            }
        })
        .catch((error) => {
            console.error("추천 카페 불러오기 오류:", error);
        });
}

function displayRecommendedCafes(cafes) {
    const container = document.getElementById("recommended-cafe-container");
    if (!container) return;
    container.innerHTML = "";
    cafes.forEach((cafe) => {
        const col = document.createElement("div");
        col.className = "col-md-4 mb-4 cafe-item";
        col.setAttribute("data-cafe-code", cafe.cafeCode);
        col.innerHTML = `
      <div class="card cafe-card">
          <img src="${cafe.imageUrl ? cafe.imageUrl : "/default-cafe.png"}" class="card-img-top" alt="${cafe.name}">
          <div class="card-body">
              <h5 class="card-title">${cafe.name}</h5>
              <p class="card-text">${cafe.address}</p>
              <div class="rating-section">
                  <span class="star-rating">${getStarRating(cafe.averageRating)}</span>
                  <span class="rating-value">(${cafe.reviewCount} 리뷰)</span>
              </div>
          </div>
      </div>
    `;
        container.appendChild(col);
    });
}
