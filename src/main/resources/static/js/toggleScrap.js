let scrappedCodes = [];

export function initScrapButtons() {
    fetch('/api/member/scrapCafes', {credentials: 'include'})
        .then(response => {
            if (!response.ok) {
                throw new Error("스크랩된 카페 정보를 가져오는데 실패했습니다.");
            }
            return response.json();
        })
        .then(data => {
            const scrappedCodes = data.data.map(item => item.cafeCode);
            const buttons = document.querySelectorAll('.btn-scrap');
            buttons.forEach(button => {
                const cafeCode = button.getAttribute("data-cafe-code");
                if (scrappedCodes.includes(cafeCode)) {
                    button.innerHTML = `<i class="fa-solid fa-heart" style="color: red;"></i> 스크랩`;
                }
            });
        })
        .catch(error => {
            console.error("스크랩 정보 초기화 오류:", error);
        });
}

export function toggleScrap(cafeCode, button) {
    fetch(`/api/member/${encodeURIComponent(cafeCode)}/scrap`, {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => {
            if (response.status === 401) {
                alert("로그인이 필요합니다");
                return refreshAccessToken()  // 새로 토큰 발급 시도
                    .then(() => {
                        // 새로 발급받은 accessToken을 사용하여 재요청
                        return fetch(`/api/member/${encodeURIComponent(cafeCode)}/scrap`, {
                            method: "POST",
                            credentials: "include",
                            headers: {
                                "Content-Type": "application/json"
                            }
                        });
                    });
            }
            if (!response.ok) {
                throw new Error("스크랩 요청 실패");
            }
            return response.json();
        })
        .then(data => {
            if (!data) return; // 401인 경우 data가 없으므로 종료
            const isScrapped = data.data;
            if (isScrapped) {
                button.innerHTML = `<i class="fa-solid fa-heart" style="color: red;"></i> 스크랩`;
            } else {
                button.innerHTML = `<i class="fa-regular fa-heart"></i> 스크랩`;
            }
        })
        .catch(error => {
            console.error("스크랩 요청 오류:", error);
            alert("스크랩 처리 중 오류가 발생했습니다.");
        });
}


function refreshAccessToken() {
    return fetch('/api/token/refreshToken', {
        method: 'GET',
        credentials: 'include' // 쿠키를 포함하여 요청
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("리프레시 토큰 요청에 실패했습니다.");
            }
            return response.json();
        })
        .then(data => {
            const {accessToken} = data.data;
            if (accessToken) {
                // 새로운 access token을 사용할 수 있도록 처리
                document.cookie = `accessToken=${accessToken}; path=/;`;  // 쿠키에 저장
            }
            return accessToken;
        });
}

