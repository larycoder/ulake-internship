
function searchReady() {
    $("#btn-search").on("click", () => {
        const query = $("input#search").val();
        if (!isEmpty(query)) {
            window.location = `/search/?query=${encodeURI(query)}`
        }
    });
}

$(document).ready(searchReady);