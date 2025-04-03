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

export function displayGuReviewStats(statsList) {
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
