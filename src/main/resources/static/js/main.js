import {checkLoginStatus, logout} from "./auth.js";
import {getStarRating, searchCafes} from "./cafeSearch.js";

window.logout = logout;

document.addEventListener("DOMContentLoaded", () => {
    fetchPopularCafes();

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

    fetchGuReviewStats();

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
            <a href="/cafe/${stat.guCode}" class="btn btn-primary" onclick="event.stopPropagation()">탐험 하기</a>
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

let map;
let userMarker; // 사용자의 현재 위치 마커
let cafeMarkers = []; // 주변 카페 마커들을 저장할 배열

// DOMContentLoaded 이벤트에서 위치 감시 시작
document.addEventListener("DOMContentLoaded", () => {
    if (navigator.geolocation) {
        navigator.geolocation.watchPosition(
            (position) => {
                const userLat = position.coords.latitude;
                const userLng = position.coords.longitude;

                // 최초 로드시 지도 생성, 이후에는 업데이트
                if (!map) {
                    initMap(userLat, userLng);
                } else {
                    updateUserMarker(userLat, userLng);
                }

                // 주변 카페 정보 업데이트
                fetchNearbyCafes(userLat, userLng);
            },
            (error) => {
                console.error("위치 정보를 가져오지 못했습니다:", error);
                alert("현재 위치를 확인할 수 없습니다. 위치 정보 제공을 허용해주세요.");
            }
        );
    } else {
        alert("이 브라우저는 위치 정보 기능을 지원하지 않습니다.");
    }
});

// 구글 맵 초기화 함수
function initMap(userLat, userLng) {
    map = new google.maps.Map(document.getElementById("map"), {
        center: {lat: userLat, lng: userLng},
        zoom: 17,
    });

    // 사용자의 현재 위치 표시 (파란색 마커)
    userMarker = new google.maps.Marker({
        position: {lat: userLat, lng: userLng},
        map: map,
        title: "내 위치",
        icon: "http://maps.google.com/mapfiles/ms/icons/blue-dot.png",
    });
}

// 사용자 위치 업데이트 함수
function updateUserMarker(userLat, userLng) {
    map.setCenter({lat: userLat, lng: userLng});
    if (userMarker) {
        userMarker.setPosition({lat: userLat, lng: userLng});
    }
}

// API를 통해 주변 카페 정보를 가져오는 함수
function fetchNearbyCafes(latitude, longitude) {
    const url = `/api/cafes/by-distance?latitude=${latitude}&longitude=${longitude}`;

    fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error("네트워크 응답이 정상이 아닙니다.");
            }
            return response.json();
        })
        .then((data) => {
            if (data.success && data.data) {
                // 기존 카페 마커 제거 (중복 표시 방지)
                cafeMarkers.forEach((marker) => marker.setMap(null));
                cafeMarkers = [];
                addCafeMarkers(data.data);
            } else {
                console.error("API 오류:", data.message);
            }
        })
        .catch((error) => {
            console.error("카페 정보를 불러오는 중 오류 발생:", error);
        });
}

// 기존 카페 마커 아이콘 변경
const cafeIcon = {
    url: "https://img.icons8.com/ios-filled/50/coffee-to-go.png",
    scaledSize: new google.maps.Size(35, 35),
};

// 지도에 카페 마커 추가하는 함수
function addCafeMarkers(cafes) {
    const container = document.getElementById("nearby-cafe-container");
    container.innerHTML = ""; // 기존 목록 초기화

    cafes.forEach((cafe) => {
        const [latStr, lngStr] = cafe.location.split(",");
        const lat = parseFloat(latStr);
        const lng = parseFloat(lngStr);

        const marker = new google.maps.Marker({
            position: {lat, lng},
            map: map,
            title: cafe.name,
            icon: cafeIcon,
        });

        // 툴팁 (구름 모양) 생성
        const infoWindow = new google.maps.InfoWindow({
            content: `
                <div class="cafe-map-tooltip">
                    <strong>${cafe.name}</strong><br>
                    ⭐ ${cafe.averageRating.toFixed(1)} / 5.0
                </div>
            `,
        });

        // 마우스 오버 시 툴팁 표시
        marker.addListener("mouseover", () => infoWindow.open(map, marker));
        marker.addListener("mouseout", () => infoWindow.close());

        marker.addListener("click", () => {
            displayCafeInfo(cafe);
        });

        cafeMarkers.push(marker);
    });
}

function displayCafeInfo(cafe) {
    const container = document.getElementById("nearby-cafe-container");
    container.innerHTML = ""; // 기존 카드 삭제

    const card = document.createElement("div");
    card.classList.add("cafe-info-card");

    card.innerHTML = `
        <img src="${cafe.imageUrl}" alt="${cafe.name}">
        <div class="cafe-details">
            <h4>${cafe.name}</h4>
            <p>${cafe.address || "주소 정보 없음"}</p>
            <div class="cafe-star-rating">${getStarRating(cafe.averageRating)}</div>
        </div>
    `;

    container.appendChild(card);
}
