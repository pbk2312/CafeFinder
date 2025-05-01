import {checkLoginStatus, logout} from "./auth.js";

window.logout = logout;

const themeImages = {
  "COZY": "https://cafefinder.s3.ap-northeast-2.amazonaws.com/img/COZY.jpg",
  "QUIET": "https://cafefinder.s3.ap-northeast-2.amazonaws.com/img/quiet.jpg",
  "STUDY_FRIENDLY": "https://cafefinder.s3.ap-northeast-2.amazonaws.com/img/study_friendly.jpg",
  "DESSERT": "https://cafefinder.s3.ap-northeast-2.amazonaws.com/img/dessert.jpg",
  "SPECIALTY_COFFEE": "https://cafefinder.s3.ap-northeast-2.amazonaws.com/img/specialty_coffee.jpg",
  "NONE": "https://cafefinder.s3.ap-northeast-2.amazonaws.com/img/none.jpg"
};

const themeIcons = {
  "COZY": "🛋️",
  "QUIET": "🤫",
  "STUDY_FRIENDLY": "📚",
  "DESSERT": "🍰",
  "SPECIALTY_COFFEE": "☕",
  "NONE": "✨"
};

document.addEventListener("DOMContentLoaded", function () {

  checkLoginStatus();

  fetch("/api/cafes/themes")
  .then(response => response.json())
  .then(result => {

    const themes = result.data;
    const container = document.getElementById("themeCardContainer");

    themes.forEach(theme => {

      const themeName = theme.name;
      const themeDesc = theme.description;

      const colDiv = document.createElement("div");
      colDiv.className = "col-md-4 mb-4";

      const cardDiv = document.createElement("div");
      cardDiv.className = "card cafe-card theme-card";
      cardDiv.style.cursor = "pointer";

      cardDiv.dataset.theme = themeName;

      const img = document.createElement("img");
      img.className = "card-img-top";
      img.alt = themeDesc;
      img.src = themeImages[themeName]
          || "https://via.placeholder.com/300x200?text=No+Image";

      const cardBody = document.createElement("div");
      cardBody.className = "card-body";

      const cardTitle = document.createElement("h5");
      cardTitle.className = "card-title";
      const icon = themeIcons[themeName] || "";
      cardTitle.textContent = icon + " " + themeDesc;

      cardBody.appendChild(cardTitle);
      cardDiv.appendChild(img);
      cardDiv.appendChild(cardBody);
      colDiv.appendChild(cardDiv);
      container.appendChild(colDiv);

      cardDiv.addEventListener("click", function () {

        document.querySelectorAll(".theme-card").forEach(
            card => card.classList.remove("selected"));

        cardDiv.classList.add("selected");

        const parts = window.location.pathname.split('/');
        const district = parts[2] || "GN";
        window.location.href = `/cafe/${district}/${cardDiv.dataset.theme}`;
      });
    });
  })
  .catch(error => console.error("테마 로드 실패:", error));
});
