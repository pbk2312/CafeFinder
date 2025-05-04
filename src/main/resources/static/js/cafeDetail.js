import {checkLoginStatus, logout} from './auth.js';

// 전역 변수: 현재 페이지, 로딩 상태, 추가 데이터 여부
let currentReviewPage = 0;
let isLoadingReviews = false;
let hasMoreReviews = true;

document.addEventListener("DOMContentLoaded", () => {
  window.logout = logout;
  checkLoginStatus();

  // 1) 서버에서 Google API 키를 받아와서 맵 스크립트 동적 로드
  fetch('/api/google-key')
  .then(res => {
    if (!res.ok) {
      throw new Error('Google API 키를 불러오지 못했습니다.');
    }
    return res.json();
  })
  .then(data => loadGoogleMaps(data.key))
  .catch(err => console.error(err));
});

function loadGoogleMaps(apiKey) {
  const script = document.createElement('script');
  script.src = `https://maps.googleapis.com/maps/api/js?key=${apiKey}`;
  script.async = true;
  script.defer = true;
  script.onload = () => {
    // Maps API 로드 완료 후 카페 정보·리뷰 로드 및 스크롤 이벤트 등록
    loadCafeDetails();
    window.addEventListener("scroll", handleScroll);
  };
  document.head.appendChild(script);
}

// 현재 URL에서 카페 코드를 추출
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
    center: {lat: 37.7749, lng: -122.4194}
  });

  const geocoder = new google.maps.Geocoder();
  geocoder.geocode({address}, (results, status) => {
    if (status === 'OK') {
      map.setCenter(results[0].geometry.location);
      new google.maps.Marker({
        map,
        position: results[0].geometry.location
      });
    } else {
      console.error('Geocode 실패:', status);
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
      loadCafeReviews(cafeCode);
    } else {
      console.error("카페 정보를 가져오는데 실패했습니다.", result.message);
    }
  })
  .catch(error => console.error("카페 정보 요청 오류:", error));
}

function loadCafeReviews(cafeCode) {
  if (isLoadingReviews || !hasMoreReviews) {
    return;
  }
  isLoadingReviews = true;

  fetch(`/api/cafes/reviews/${cafeCode}?page=${currentReviewPage}`)
  .then(response => response.json())
  .then(result => {
    if (result.success && result.data) {
      if (!result.data.reviews || result.data.reviews.length === 0) {
        hasMoreReviews = false;
      } else {
        populateCafeReviews(result.data);
        currentReviewPage++;
      }
    } else {
      console.error("리뷰 정보를 가져오는데 실패했습니다.", result.message);
    }
  })
  .catch(error => console.error("리뷰 정보 요청 오류:", error))
  .finally(() => {
    isLoadingReviews = false;
  });
}

function populateCafeDetails(data) {
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

  const averageRatingEl = document.getElementById("cafe-review");
  if (data.averageRating != null) {
    averageRatingEl.innerHTML = `<strong>평균 평점:</strong> ${data.averageRating.toFixed(
        1)} ${generateStarRating(data.averageRating)}`;
  } else {
    averageRatingEl.innerHTML = `<strong>평균 평점:</strong> 평가 정보가 없습니다.`;
  }

  const phoneEl = document.getElementById("cafe-phone");
  if (data.phoneNumber) {
    phoneEl.innerHTML = `<strong>전화번호:</strong> ${data.phoneNumber}`;
    phoneEl.style.display = "block";
  } else {
    phoneEl.style.display = "none";
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
    data.themes.forEach(code => {
      const span = document.createElement("span");
      span.className = "badge-theme";
      span.style = themeStyles[code] || themeStyles.NONE;
      span.innerText = themeDisplayNames[code] || "기타";
      themesContainer.appendChild(span);
    });
  } else {
    themesContainer.style.display = "none";
  }

  if (data.address) {
    showMapForAddress(data.address);
  }
}

function populateCafeReviews(data) {
  const reviewCountSpan = document.getElementById("review-count");
  if (currentReviewPage === 0 && data.reviewCount != null) {
    reviewCountSpan.innerText = data.reviewCount;
  }

  const reviewsContainer = document.getElementById("reviews");
  if (data.reviews && data.reviews.length > 0) {
    data.reviews.forEach(review => {
      const card = document.createElement("div");
      card.className = "review-card";
      card.innerHTML = `
                <div class="review-rating">
                    ${generateStarRating(
          review.rating)}<span class="rating-text">${review.rating}</span>
                </div>
                <p class="review-text">${review.review}</p>
            `;
      reviewsContainer.appendChild(card);
    });
  } else if (currentReviewPage === 0) {
    reviewsContainer.innerHTML = "<p>등록된 리뷰가 없습니다.</p>";
  }
}

function handleScroll() {
  if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight
      - 200) {
    const cafeCode = getCafeCodeFromUrl();
    if (cafeCode) {
      loadCafeReviews(cafeCode);
    }
  }
}
