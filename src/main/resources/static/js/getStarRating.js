export function getStarRating(rating) {
    const fullStars = Math.floor(rating);
    const halfStar = (rating - fullStars) >= 0.5 ? 1 : 0;
    const emptyStars = 5 - fullStars - halfStar;
    let starsHtml = '';
    for (let i = 0; i < fullStars; i++) {
        starsHtml += '<i class="fa-solid fa-star" style="color: gold;"></i>';
    }
    if (halfStar) {
        starsHtml += '<i class="fa-solid fa-star-half-alt" style="color: gold;"></i>';
    }
    for (let i = 0; i < emptyStars; i++) {
        starsHtml += '<i class="fa-regular fa-star" style="color: gold;"></i>';
    }
    return starsHtml;
}
