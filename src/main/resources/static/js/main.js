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

  const recommendedContainer = document.getElementById(
      "recommended-cafe-container");
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

  const searchResultsContainer = document.getElementById(
      "search-results-container");
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
    if (!searchInput) {
      return;
    }
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

// main.js

let map;
let userMarker;              // 사용자의 현재 위치 마커
let cafeMarkers = [];         // 주변 카페 마커들을 저장할 배열

// 🛠️ 1) 아이콘 변수를 최상단에 선언만 해 둡니다.
let defaultCafeIcon;
let selectedCafeIcon;

// DOMContentLoaded 이벤트에서 위치 감시 및 Google API 로드 시작
document.addEventListener("DOMContentLoaded", () => {
  fetch('/api/google-key')
  .then(res => {
    if (!res.ok) {
      throw new Error('키 로드 실패');
    }
    return res.json();
  })
  .then(data => {
    // Google Maps JS API 스크립트를 동적으로 생성·삽입
    const s = document.createElement('script');
    s.src = `https://maps.googleapis.com/maps/api/js?key=${data.key}`;
    s.async = true;

    s.onload = () => {
      // 🛠️ 2) API 로드된 시점에 아이콘 객체를 초기화
      defaultCafeIcon = {
        url: "https://img.icons8.com/ios-filled/50/coffee-to-go.png",
        scaledSize: new google.maps.Size(35, 35),
      };
      selectedCafeIcon = {
        url: "https://img.icons8.com/ios-filled/50/000000/coffee-to-go.png",
        scaledSize: new google.maps.Size(45, 45),
      };

      // 스크립트가 준비된 후에야 지오로케이션 감시 시작
      if (navigator.geolocation) {
        navigator.geolocation.watchPosition(
            position => {
              const lat = position.coords.latitude;
              const lng = position.coords.longitude;

              if (!map) {
                initMap(lat, lng);
              } else {
                updateUserMarker(lat, lng);
              }
              fetchNearbyCafes(lat, lng);
            },
            err => {
              console.error('위치 오류:', err);
              alert('위치 정보를 가져올 수 없습니다.');
            }
        );
      } else {
        alert('이 브라우저는 위치 기능을 지원하지 않습니다.');
      }
    };

    document.head.appendChild(s);
  })
  .catch(console.error);
});

// 구글 맵 초기화 함수
function initMap(userLat, userLng) {
  map = new google.maps.Map(document.getElementById("map"), {
    center: {lat: userLat, lng: userLng},
    zoom: 17,
  });

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
    headers: {"Content-Type": "application/json"},
  })
  .then(response => {
    if (!response.ok) {
      throw new Error("네트워크 응답이 정상이 아닙니다.");
    }
    return response.json();
  })
  .then(data => {
    if (data.success && data.data) {
      // 기존 카페 마커 제거 (중복 표시 방지)
      cafeMarkers.forEach(m => m.setMap(null));
      cafeMarkers = [];
      addCafeMarkers(data.data);
    } else {
      console.error("API 오류:", data.message);
    }
  })
  .catch(error => {
    console.error("카페 정보를 불러오는 중 오류 발생:", error);
  });
}

// 카페 마커 생성 및 이벤트 바인딩
function addCafeMarkers(cafes) {
  const container = document.getElementById("nearby-cafe-container");
  if (!container) {
    console.error("nearby-cafe-container가 없습니다.");
    return;
  }
  container.innerHTML = ""; // 기존 목록 초기화

  cafes.forEach(cafe => {
    const [latStr, lngStr] = cafe.location.split(",");
    const lat = parseFloat(latStr);
    const lng = parseFloat(lngStr);

    const marker = new google.maps.Marker({
      position: {lat, lng},
      map: map,
      title: cafe.name,
      icon: defaultCafeIcon,
    });

    const infoWindow = new google.maps.InfoWindow({
      content: `
        <div class="cafe-map-tooltip">
          <strong>${cafe.name}</strong><br>
          ⭐ ${cafe.averageRating.toFixed(1)} / 5.0
        </div>
      `,
    });

    marker.addListener("mouseover", () => infoWindow.open(map, marker));
    marker.addListener("mouseout", () => infoWindow.close());
    marker.addListener("click", () => {
      marker.setAnimation(google.maps.Animation.BOUNCE);
      setTimeout(() => marker.setAnimation(null), 700);
      marker.setIcon(selectedCafeIcon);
      displayCafeInfo(cafe);
    });

    cafeMarkers.push(marker);
  });
}
