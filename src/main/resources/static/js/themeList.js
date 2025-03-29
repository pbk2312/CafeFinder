// 테마별 카드에 사용할 이미지 매핑 (예시)
const themeImages = {
    "COZY": "/img/cozy.jpg",
    "QUIET": "/img/quiet.jpg",
    "STUDY_FRIENDLY": "/img/study_friendly.jpg",
    "DESSERT": "/img/dessert.jpg",
    "SPECIALTY_COFFEE": "/img/specialty_coffee.jpg",
    "NONE": "/img/none.jpg"
};

// 각 테마의 성격에 맞는 아이콘 매핑
const themeIcons = {
    "COZY": "🛋️",
    "QUIET": "🤫",
    "STUDY_FRIENDLY": "📚",
    "DESSERT": "🍰",
    "SPECIALTY_COFFEE": "☕",
    "NONE": "✨"
};

document.addEventListener("DOMContentLoaded", function () {
    // '/api/cafes/theme' 엔드포인트 호출
    fetch("/api/cafes/theme")
        .then(response => response.json())
        .then(result => {
            // result = { message, data, success }
            const themes = result.data;
            const container = document.getElementById("themeCardContainer");

            themes.forEach(theme => {
                // theme = { name: "COZY", description: "분위기 좋은" } 등
                const themeName = theme.name;
                const themeDesc = theme.description;

                // 카드 생성
                const colDiv = document.createElement("div");
                colDiv.className = "col-md-4 mb-4";

                const cardDiv = document.createElement("div");
                cardDiv.className = "card cafe-card theme-card";
                cardDiv.style.cursor = "pointer";
                // data-theme 속성에 영어 Enum 이름 저장
                cardDiv.dataset.theme = themeName;

                const img = document.createElement("img");
                img.className = "card-img-top";
                img.alt = themeDesc;
                img.src = themeImages[themeName] || "https://via.placeholder.com/300x200?text=No+Image";

                const cardBody = document.createElement("div");
                cardBody.className = "card-body";

                // 카드 제목: 설명과 각 테마에 맞는 아이콘만 표시
                const cardTitle = document.createElement("h5");
                cardTitle.className = "card-title";
                const icon = themeIcons[themeName] || "";
                cardTitle.textContent = icon + " " + themeDesc;

                // 테마 이름은 표시하지 않음
                cardBody.appendChild(cardTitle);
                cardDiv.appendChild(img);
                cardDiv.appendChild(cardBody);
                colDiv.appendChild(cardDiv);
                container.appendChild(colDiv);

                // 클릭 시 선택 상태 표시 및 /cafe/{district}/{themeName} 페이지로 이동
                cardDiv.addEventListener("click", function () {
                    // 기존 선택 해제
                    document.querySelectorAll(".theme-card").forEach(card => card.classList.remove("selected"));
                    // 현재 카드에 selected 표시
                    cardDiv.classList.add("selected");
                    // URL의 세 번째 부분을 district로 사용 (없으면 기본값 "GN")
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
