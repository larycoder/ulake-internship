import { tableApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

function drawTable(resp) {
    const header = $("thead tr")
    $("#name-detail").html(`Content for table "${resp.name}"`);

    resp.columns.forEach(col => {
        const th = $("<th></th>");
        th.html(col);
        header.append(th);
    });
    // post process each row
    let rows = [];
    for (const rid in resp.rows) {
        resp.rows[rid] = resp.rows[rid].map(cell => (`<input type="text" class="form-control form-control-sm" value="${cell}">`));
        rows.push(resp.rows[rid]);
    }
    console.log(rows);
    $.fn.dataTable.ext.errMode = 'none';
    $('#table').DataTable({
        bProcessing: true,
        paging: true,
        ordering: false,
        data: rows
    });
}

async function ready() {
    const params = parseParam("id", "/tables");
    const id = parseInt(params.id);
    const data = await tableApi.data(id);
    drawTable(data);
}

$(document).ready(() => ready());
