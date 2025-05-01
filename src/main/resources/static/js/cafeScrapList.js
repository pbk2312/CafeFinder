// /js/cafeScrapList.js
import {initScrapButtons, toggleScrap} from './toggleScrap.js';
import {createCafeCard} from './displaySeacrhCards.js';

document.addEventListener("DOMContentLoaded", loadScrappedCafes);

function showSpinner(container) {
  container.innerHTML = `
    <div class="d-flex justify-content-center align-items-center my-5 w-100">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">로딩 중...</span>
      </div>
    </div>`;
}

function loadScrappedCafes() {
  const container = document.getElementById("scrap-container");
  showSpinner(container);

  fetch("/api/member/cafeScrapsList", {credentials: 'include'})
  .then(res => {
    if (!res.ok) {
      throw new Error("스크랩 목록을 불러오는 중 오류");
    }
    return res.json();
  })
  .then(resp => displayScrapResults(resp.data))
  .catch(err => {
    console.error(err);
    container.innerHTML = `
        <div class="alert alert-danger text-center my-5 w-100">
          스크랩 목록을 불러오는 데 실패했습니다.<br>잠시 후 다시 시도해주세요.
        </div>`;
  });
}

function displayScrapResults(cafeList) {
  const container = document.getElementById("scrap-container");
  container.innerHTML = "";

  if (!Array.isArray(cafeList) || cafeList.length === 0) {
    container.innerHTML = `
      <div class="text-center text-muted my-5 w-100">
        <i class="bi bi-bookmark-x" style="font-size: 2rem;"></i>
        <p class="mt-3">스크랩한 카페가 없습니다.</p>
      </div>`;
    return;
  }

  cafeList.forEach(cafe => {
    // Bootstrap 반응형 1~4열
    const col = document.createElement('div');
    col.className = 'col-12 col-sm-6 col-md-4 col-lg-3 mb-4 cafe-item';
    col.setAttribute('data-cafe-code', cafe.cafeCode);

    // 보여주신 createCafeCard 함수는 div.col을 반환하므로
    // 대신 카드 내부만 그려주는 createCafeCardBody 같은 함수로 분리했다면 좋지만,
    // 여기서는 createCafeCard가 col을 반환하니 그대로 append
    const cardCol = createCafeCard(cafe);
    col.appendChild(cardCol.querySelector('.card'));

    container.appendChild(col);
  });

  // 스크랩 버튼 초기화 및 이벤트 바인딩
  initScrapButtons();
  container.querySelectorAll('.btn-scrap').forEach(btn => {
    btn.addEventListener('click', e => {
      e.stopPropagation();
      toggleScrap(btn.dataset.cafeCode, btn);
    });
  });

  // 카드 클릭 시 상세 페이지 이동
  container.querySelectorAll('.cafe-item').forEach(cardWrapper => {
    cardWrapper.addEventListener('click', async () => {
      const code = cardWrapper.dataset.cafeCode;
      try {
        await fetch(`/api/cafes/click/${code}`, {
          method: 'POST',
          credentials: 'include'
        });
      } catch {
        /* 무시 */
      } finally {
        window.location.href = `/cafe/detail/${code}`;
      }
    });
  });
}
