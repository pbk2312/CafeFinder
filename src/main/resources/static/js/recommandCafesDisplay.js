import {getStarRating} from "./getStarRating.js";

export function displayRecommendedCafes(cafes) {
  const container = document.getElementById("recommended-cafe-container");
  if (!container) {
    return;
  }
  container.innerHTML = "";
  cafes.forEach((cafe) => {
    const col = document.createElement("div");
    col.className = "col-md-4 mb-4 cafe-item";
    col.setAttribute("data-cafe-code", cafe.cafeCode);
    col.innerHTML = `
      <div class="card cafe-card">
          <img src="${cafe.imageUrl ? cafe.imageUrl
        : "https://cafefinder.s3.ap-northeast-2.amazonaws.com/img/coffee-2714970_1280.jpg"}" class="card-img-top" alt="${cafe.name}">
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
