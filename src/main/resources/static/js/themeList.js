const themeImages = {
    "COZY": "/img/cozy.jpg",
    "QUIET": "/img/quiet.jpg",
    "STUDY_FRIENDLY": "/img/study_friendly.jpg",
    "DESSERT": "/img/dessert.jpg",
    "SPECIALTY_COFFEE": "/img/specialty_coffee.jpg",
    "NONE": "/img/none.jpg"
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

    fetch("/api/cafes/theme")
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
                img.src = themeImages[themeName] || "https://via.placeholder.com/300x200?text=No+Image";

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

                    document.querySelectorAll(".theme-card").forEach(card => card.classList.remove("selected"));

                    cardDiv.classList.add("selected");

                    const parts = window.location.pathname.split('/');
                    const district = parts[2] || "GN";
                    window.location.href = `/cafe/${district}/${cardDiv.dataset.theme}`;
                });
            });
        })
        .catch(error => console.error("테마 로드 실패:", error));
});

document.addEventListener("DOMContentLoaded", function () {
    checkLoginStatus();
});

function checkLoginStatus() {
    fetch("/api/member/validateToken", {
        method: "GET",
        credentials: "include"
    })
        .then(response => response.json())
        .then(data => {
            if (data.success && data.data) {
                updateNavbarForLoggedInUser(data.data.nickName, data.data.profileImagePath);
            }
        })
        .catch(error => console.error("로그인 상태 확인 오류:", error));
}

function updateNavbarForLoggedInUser(nickName, profileImagePath) {
    const navbar = document.getElementById("navbarNav");
    const loginLink = document.getElementById("login-link");
    if (loginLink) loginLink.remove();

    const profileImageTag = profileImagePath
        ? `<img src="${profileImagePath}" class="rounded-circle" width="40" height="40" alt="프로필 이미지">`
        : `<img src="/default-profile.png" class="rounded-circle" width="40" height="40" alt="기본 프로필">`;

    navbar.innerHTML += `
        <li class="nav-item d-flex align-items-center me-3">
          <a class="nav-link" href="#" onclick="logout()">로그아웃</a>
        </li>
        <li class="nav-item d-flex align-items-center">
          <a class="nav-link d-flex align-items-center" href="/member/profile">
            ${profileImageTag} <span class="ms-2 fw-semibold">${nickName}</span>
          </a>
        </li>
    `;
}

function logout() {
    fetch("/api/member/logout", {
        method: "POST",
        credentials: "include"
    })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                console.error("로그아웃 실패");
            }
        })
        .catch(error => console.error("로그아웃 요청 오류:", error));
}
