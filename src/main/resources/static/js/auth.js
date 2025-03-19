export function checkLoginStatus() {
    return fetch("/api/member/validateToken", {
        method: "GET",
        credentials: "include"
    })
        .then(response => response.json())
        .then(data => {
            if (data.success && data.data) {
                updateNavbarForLoggedInUser(data.data.nickName, data.data.profileImagePath);
                return true;
            } else {
                return false;
            }
        })
        .catch(error => {
            console.error("로그인 상태 확인 오류:", error);
            return false;
        });
}

export function updateNavbarForLoggedInUser(nickName, profileImagePath) {
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

export function logout() {
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
