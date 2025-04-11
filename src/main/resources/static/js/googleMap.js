import {getStarRating} from "./getStarRating.js"

export function displayCafeInfo(cafe) {
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

    // 카드 클릭 시 상세 페이지로 이동
    card.addEventListener("click", () => {
        window.location.href = `/cafe/detail/${cafe.cafeCode}`;
    });

    container.appendChild(card);
}
