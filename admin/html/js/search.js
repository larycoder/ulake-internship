
function querySearch() {
    const query = $("input#search").val();
    if (!isEmpty(query)) {
        window.location = `/search/?query=${encodeURI(query)}`
    }
}
function searchReady() {
    $("#btn-search")
        .on("click", querySearch)
        .on("keypress", e => {
            if (e.which === 13) querySearch();
        });
}

$(document).ready(searchReady);