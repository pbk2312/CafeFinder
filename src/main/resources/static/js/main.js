import {checkLoginStatus, logout} from "./auth.js";
import {searchCafes} from "./cafeSearch.js"
import {displayPopularCafes} from "./popularCafes.js";
import {seoulDistrictStatus} from "./districtDisplay.js";
import {displayRecommendedCafes} from "./recommandCafesDisplay.js";
import {displayCafeInfo} from "./googleMap.js";

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

    fetchSeoulDistrictStatus();


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

// 처음 검색버튼 눌렀을 때
export const searchForm = document.getElementById("cafe-search-form");
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
        // 검색어가 있으면 호출
        searchCafes(searchValue, 0);
    });
}

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

// ── 구 리뷰 통계 관련 함수 ──
function fetchSeoulDistrictStatus() {
    fetch("/api/seoulDistrictStatus")
        .then((response) => response.json())
        .then((data) => {
            if (data.success) {
                seoulDistrictStatus(data.data);
                updateStarRatings();
            }
        })
        .catch((error) => console.error("구 리뷰 통계 가져오기 오류:", error));
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


let map;
let userMarker; // 사용자의 현재 위치 마커
let cafeMarkers = []; // 주변 카페 마커들을 저장할 배열

// DOMContentLoaded 이벤트에서 위치 감시 시작
document.addEventListener("DOMContentLoaded", () => {
    console.log("DOMContentLoaded 이벤트 발생");
    if (navigator.geolocation) {
        navigator.geolocation.watchPosition(
            (position) => {
                const userLat = position.coords.latitude;
                const userLng = position.coords.longitude;
                console.log("📍 위치 수신됨:", userLat, userLng);

                // 최초 로드시 지도 생성, 이후에는 업데이트
                if (!map) {
                    console.log("지도 초기화 진행");
                    initMap(userLat, userLng);
                } else {
                    console.log("사용자 마커 업데이트 진행");
                    updateUserMarker(userLat, userLng);
                }
                // 주변 카페 정보 업데이트
                console.log("주변 카페 정보 요청");
                fetchNearbyCafes(userLat, userLng);
            },
            (error) => {
                console.error("위치 정보를 가져오지 못했습니다:", error.code, error.message);
                if (error.code === error.PERMISSION_DENIED) {
                    alert("위치 정보 사용이 거부되었습니다.");
                } else if (error.code === error.POSITION_UNAVAILABLE) {
                    alert("현재 위치를 확인할 수 없습니다. 위치 서비스가 활성화되어 있는지 확인해 주세요.");
                } else if (error.code === error.TIMEOUT) {
                    alert("위치 정보를 가져오는 데 시간이 너무 오래 걸립니다. 다시 시도해 주세요.");
                }
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

const defaultCafeIcon = {
    url: "https://img.icons8.com/ios-filled/50/coffee-to-go.png",
    scaledSize: new google.maps.Size(35, 35),
};

const selectedCafeIcon = {
    url: "https://img.icons8.com/ios-filled/50/000000/coffee-to-go.png", // 색상이나 이미지 변경
    scaledSize: new google.maps.Size(45, 45),
};

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
            icon: defaultCafeIcon,
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
            // 클릭 시 애니메이션 적용 (바운스)
            marker.setAnimation(google.maps.Animation.BOUNCE);
            setTimeout(() => marker.setAnimation(null), 700);

            // 클릭한 마커의 아이콘 변경
            marker.setIcon(selectedCafeIcon);

            // 상세 정보 표시 (페이지 이동 등)
            displayCafeInfo(cafe);
        });

        cafeMarkers.push(marker);
    });
}
