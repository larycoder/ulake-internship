function sidebarReady() {
    $(`a[class*='nav-link'][href='${window.location.pathname}']`).parent("li").addClass("active");
    if (getGroups().includes("Admin")) {
        $("#sidebar-admin").show();
    }
}

$(document).ready(sidebarReady);