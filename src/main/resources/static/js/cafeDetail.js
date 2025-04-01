import {checkLoginStatus, logout} from './auth.js';


document.addEventListener("DOMContentLoaded", () => {

    window.logout = logout;

    checkLoginStatus();
    loadCafeDetails();
});


function getCafeCodeFromUrl() {
    const pathParts = window.location.pathname.split('/');
    return pathParts[pathParts.length - 1] || null;
}


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


function populateCafeDetails(data) {
    // 카페 이름
    const nameEl = document.getElementById("cafe-name");
    if (data.name) {
        nameEl.innerText = data.name;
        nameEl.style.display = "block";
    } else {
        nameEl.style.display = "none";
    }


    const addressEl = document.getElementById("cafe-address");
    if (data.address) {
        addressEl.innerHTML = `<i class="bi bi-geo-alt-fill me-2"></i>${data.address}`;
        addressEl.style.display = "block";
    } else {
        addressEl.style.display = "none";
    }


    const hoursEl = document.getElementById("cafe-hours");
    if (data.openingHours) {
        hoursEl.innerHTML = `<strong>영업시간:</strong> ${data.openingHours}`;
        hoursEl.style.display = "block";
    } else {
        hoursEl.style.display = "none";
    }


    const phoneEl = document.getElementById("cafe-phone");
    if (data.phoneNumber) {
        phoneEl.innerHTML = `<strong>전화번호:</strong> ${data.phoneNumber}`;
        phoneEl.style.display = "block";
    } else {
        phoneEl.style.display = "none";
    }


    const reviewEl = document.getElementById("cafe-review");
    if (data.averageRating !== null && data.averageRating !== undefined) {
        reviewEl.innerHTML = `<strong>평균 평점:</strong> ${generateStarRating(data.averageRating)}<span class="rating-text">${data.averageRating}</span>`;
        reviewEl.style.display = "block";
    } else {
        reviewEl.style.display = "none";
    }


    const imageEl = document.getElementById("cafe-image");
    if (data.imageUrl) {
        imageEl.src = data.imageUrl;
        imageEl.style.display = "block";
    } else {
        imageEl.style.display = "none";
    }


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


    const reviewCountSpan = document.getElementById("review-count");
    reviewCountSpan.innerText = (data.reviewCount !== undefined && data.reviewCount !== null) ? data.reviewCount : 0;


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


    showMapForAddress(data.address);
}
