// auth.js에 정의된 로그인 관련 함수와 로그아웃 함수를 가져옵니다.
import {checkLoginStatus, logout} from '/js/auth.js';

// DOMContentLoaded 시 초기화
document.addEventListener("DOMContentLoaded", () => {
    // logout 함수를 전역(window)에 등록 (인라인 이벤트 사용 시 필요)
    window.logout = logout;

    checkLoginStatus();
    loadCafeDetails();
});

// URL의 마지막 부분에서 카페 코드를 추출하는 함수
function getCafeCodeFromUrl() {
    const pathParts = window.location.pathname.split('/');
    return pathParts[pathParts.length - 1] || null;
}

// 평점 표시를 위한 함수 (Bootstrap Icons 사용)
function generateStarRating(rating) {
    const fullStars = Math.floor(rating);
    const halfStar = (rating - fullStars) >= 0.5 ? 1 : 0;
    const emptyStars = 5 - fullStars - halfStar;
    let starsHTML = "";
    for (let i = 0; i < fullStars; i++) {
        starsHTML += '<i class="bi bi-star-fill" style="color: var(--star-color);"></i>';
    }
    if (halfStar) {
        starsHTML += '<i class="bi bi-star-half" style="color: var(--star-color);"></i>';
    }
    for (let i = 0; i < emptyStars; i++) {
        starsHTML += '<i class="bi bi-star" style="color: var(--star-color);"></i>';
    }
    return starsHTML;
}

// Google Maps API를 이용해 주소 기반으로 지도와 마커 표시
function showMapForAddress(address) {
    const mapEl = document.getElementById('map');
    const map = new google.maps.Map(mapEl, {
        zoom: 15,
        center: {lat: 37.7749, lng: -122.4194} // 기본 좌표 (예: 샌프란시스코)
    });

    const geocoder = new google.maps.Geocoder();
    geocoder.geocode({address: address}, function (results, status) {
        if (status === 'OK') {
            map.setCenter(results[0].geometry.location);
            new google.maps.Marker({
                map: map,
                position: results[0].geometry.location
            });
        } else {
            console.error('Geocode 실패: ' + status);
        }
    });
}

// 카페 상세 정보를 서버에서 불러옵니다.
function loadCafeDetails() {
    const cafeCode = getCafeCodeFromUrl();
    if (!cafeCode) {
        console.error("카페 코드를 찾을 수 없습니다.");
        return;
    }
    fetch(`/api/cafes/${cafeCode}`)
        .then(response => response.json())
        .then(result => {
            if (result.success && result.data) {
                populateCafeDetails(result.data);
            } else {
                console.error("카페 정보를 가져오는데 실패했습니다.", result.message);
            }
        })
        .catch(error => console.error("카페 정보 요청 오류:", error));
}

// 서버에서 받아온 데이터로 카페 상세 정보를 채웁니다.
function populateCafeDetails(data) {
    // 카페 이름
    const nameEl = document.getElementById("cafe-name");
    if (data.name) {
        nameEl.innerText = data.name;
        nameEl.style.display = "block";
    } else {
        nameEl.style.display = "none";
    }

    // 카페 주소
    const addressEl = document.getElementById("cafe-address");
    if (data.address) {
        addressEl.innerHTML = `<i class="bi bi-geo-alt-fill me-2"></i>${data.address}`;
        addressEl.style.display = "block";
    } else {
        addressEl.style.display = "none";
    }

    // 영업시간 (openingHours)
    const hoursEl = document.getElementById("cafe-hours");
    if (data.openingHours) {
        hoursEl.innerHTML = `<strong>영업시간:</strong> ${data.openingHours}`;
        hoursEl.style.display = "block";
    } else {
        hoursEl.style.display = "none";
    }

    // 전화번호 (phoneNumber)
    const phoneEl = document.getElementById("cafe-phone");
    if (data.phoneNumber) {
        phoneEl.innerHTML = `<strong>전화번호:</strong> ${data.phoneNumber}`;
        phoneEl.style.display = "block";
    } else {
        phoneEl.style.display = "none";
    }

    // 평균 평점 (averageRating)
    const reviewEl = document.getElementById("cafe-review");
    if (data.averageRating !== null && data.averageRating !== undefined) {
        reviewEl.innerHTML = `<strong>평균 평점:</strong> ${generateStarRating(data.averageRating)}<span class="rating-text">${data.averageRating}</span>`;
        reviewEl.style.display = "block";
    } else {
        reviewEl.style.display = "none";
    }

    // 카페 이미지
    const imageEl = document.getElementById("cafe-image");
    if (data.imageUrl) {
        imageEl.src = data.imageUrl;
        imageEl.style.display = "block";
    } else {
        imageEl.style.display = "none";
    }

    // 테마 처리
    const themesContainer = document.getElementById("cafe-themes");
    themesContainer.innerHTML = "";
    if (data.themes && data.themes.length > 0) {
        themesContainer.style.display = "block";
        data.themes.forEach(themeCode => {
            const span = document.createElement("span");
            span.className = "badge-theme";
            // 테마별 스타일 지정 (없으면 기본 스타일 적용)
            const themeStyles = {
                COZY: 'background-color: #FAD7A0; color: #6F4E37;',
                QUIET: 'background-color: #D5F5E3; color: #27AE60;',
                STUDY_FRIENDLY: 'background-color: #AED6F1; color: #2E86C1;',
                DESSERT: 'background-color: #F9E79F; color: #B7950B;',
                SPECIALTY_COFFEE: 'background-color: #F5B7B1; color: #C0392B;',
                NONE: 'background-color: #D7DBDD; color: #7B7D7D;'
            };
            const themeDisplayNames = {
                COZY: "분위기 좋은",
                QUIET: "조용한",
                STUDY_FRIENDLY: "공부하기 좋은",
                DESSERT: "디저트 맛집",
                SPECIALTY_COFFEE: "커피 맛집",
                NONE: "기타"
            };
            span.style = themeStyles[themeCode] || themeStyles.NONE;
            span.innerText = themeDisplayNames[themeCode] || "기타";
            themesContainer.appendChild(span);
        });
    } else {
        themesContainer.style.display = "none";
    }

    // 리뷰 수 표시
    const reviewCountSpan = document.getElementById("review-count");
    reviewCountSpan.innerText = (data.reviewCount !== undefined && data.reviewCount !== null) ? data.reviewCount : 0;

    // 리뷰 표시
    const reviewsContainer = document.getElementById("reviews");
    reviewsContainer.innerHTML = "";
    if (data.reviews && data.reviews.length > 0) {
        data.reviews.forEach(review => {
            const card = document.createElement("div");
            card.className = "review-card";
            card.innerHTML = `
        <div class="review-rating">
          ${generateStarRating(review.rating)}<span class="rating-text">${review.rating}</span>
        </div>
        <p class="review-text">${review.review}</p>
      `;
            reviewsContainer.appendChild(card);
        });
    } else {
        reviewsContainer.innerHTML = "<p>등록된 리뷰가 없습니다.</p>";
    }

    // 지도에 카페 위치 표시
    showMapForAddress(data.address);
}
