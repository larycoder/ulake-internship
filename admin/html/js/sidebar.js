function toastReady() {
    $(`a[class*='nav-link'][href='${window.location.pathname}']`).parent("li").addClass("active");
}

$(document).ready(toastReady);