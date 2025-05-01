import {getStarRating} from "./getStarRating.js";
import {themeDescriptions, themeStyles} from "./theme.js";

export function createCafeCard(cafe) {
  const col = document.createElement("div");
  col.className = "col-md-4 mb-4 cafe-item";
  col.setAttribute("data-cafe-code", cafe.cafeCode);
  col.innerHTML = `
    <div class="card h-100 cafe-card">
      <img src="${cafe.imageUrl ? cafe.imageUrl
      : 'https://cafefinder.s3.ap-northeast-2.amazonaws.com/img/coffee-2714970_1280.jpg'}" class="card-img-top" alt="${cafe.name}">
      <div class="card-body" style="position: relative;">
        <h5 class="card-title">${cafe.name}</h5>
        <p class="card-text">${cafe.address}</p>
        ${cafe.openingHours
      ? `<p class="card-text"><small class="text-muted">${cafe.openingHours}</small></p>`
      : ''}
        ${cafe.phoneNumber
      ? `<p class="card-text"><small class="text-muted">전화번호: ${cafe.phoneNumber}</small></p>`
      : ''}
        <p class="card-text">
          <small class="text-muted">
            평점: <span class="star-rating">${getStarRating(cafe.averageRating)}</span>
          </small>
        </p>
        <p class="card-text">
          ${
      cafe.themes && Array.isArray(cafe.themes)
          ? cafe.themes.map(
              t => `<span class="badge theme-badge" style="${themeStyles[t]
              || 'background-color: #ccc; color: #fff;'}">${themeDescriptions[t]
              || t}</span>`).join('')
          : '<span class="badge theme-badge" style="background-color: #ccc; color: #fff;">기타</span>'
  }
        </p>
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
      </div>
    </div>
  `;
  return col;
}
