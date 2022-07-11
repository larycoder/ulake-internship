function sidebarReady() {
    $(`a[class*='nav-link'][href='${window.location.pathname}']`).parent("li").addClass("active");
}

$(document).ready(sidebarReady);