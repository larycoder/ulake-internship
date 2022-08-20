import { tableApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

function summaryTable(resp) {
    const header = $("thead tr")
    $("#name-detail").html(`Summary for environmental table "${resp.name}"`);

    resp.columns.forEach(col => {
        const th = $("<th></th>");
        th.html(col);
        header.append(th);
    });
    let rows = [];
    for (const rid in resp.rows) {
        rows.push(resp.rows[rid]);
    }
    // post process each row
    $.fn.dataTable.ext.errMode = 'none';
    $('#table').DataTable({
        bProcessing: true,
        searching: false,
        paging: false,
        ordering: false,
        data: rows
    });
}

async function ready() {
    const params = parseParam("id", "/tables");
    const id = parseInt(params.id);
    const data = await tableApi.data(id);
    summaryTable(data);
}



$(document).ready(() => ready());
