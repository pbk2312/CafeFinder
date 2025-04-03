export function renderPagination(totalPages, currentPage, mode = "district", searchCafes, currentSearchValue) {
    const paginationContainer = document.getElementById("pagination-container");
    paginationContainer.innerHTML = "";

    const ul = document.createElement("ul");
    ul.className = "pagination justify-content-center";

    // 이전 페이지 버튼
    const prevItem = document.createElement("li");
    prevItem.className = "page-item" + (currentPage <= 0 ? " disabled" : "");
    const prevLink = document.createElement("a");
    prevLink.className = "page-link";
    prevLink.href = "#";
    prevLink.innerHTML = "&laquo;";
    prevLink.addEventListener("click", function (e) {
        e.preventDefault();
        if (currentPage > 0) {
            if (mode === "search") {
                searchCafes(currentSearchValue, currentPage - 1);
            } else {
                window.location.href = `?page=${currentPage - 1}`;
            }
        }
    });
    prevItem.appendChild(prevLink);
    ul.appendChild(prevItem);

    // 페이지 번호 버튼
    const startPage = Math.floor(currentPage / 10) * 10;
    let endPage = startPage + 9;
    if (endPage >= totalPages) {
        endPage = totalPages - 1;
    }
    for (let i = startPage; i <= endPage; i++) {
        const pageItem = document.createElement("li");
        pageItem.className = "page-item" + (i === currentPage ? " active" : "");
        const pageLink = document.createElement("a");
        pageLink.className = "page-link";
        pageLink.href = "#";
        pageLink.innerText = i + 1;
        pageLink.addEventListener("click", function (e) {
            e.preventDefault();
            if (mode === "search") {
                searchCafes(currentSearchValue, i);
            } else {
                window.location.href = `?page=${i}`;
            }
        });
        pageItem.appendChild(pageLink);
        ul.appendChild(pageItem);
    }

    // 다음 페이지 버튼
    const nextItem = document.createElement("li");
    nextItem.className = "page-item" + (currentPage >= totalPages - 1 ? " disabled" : "");
    const nextLink = document.createElement("a");
    nextLink.className = "page-link";
    nextLink.href = "#";
    nextLink.innerHTML = "&raquo;";
    nextLink.addEventListener("click", function (e) {
        e.preventDefault();
        if (currentPage < totalPages - 1) {
            if (mode === "search") {
                searchCafes(currentSearchValue, currentPage + 1);
            } else {
                window.location.href = `?page=${currentPage + 1}`;
            }
        }
    });
    nextItem.appendChild(nextLink);
    ul.appendChild(nextItem);

    paginationContainer.appendChild(ul);
}
