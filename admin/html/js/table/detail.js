import { tableApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

function drawTable(resp) {
    const header = $("thead tr")
    $("#name-detail").html(`Content for table "${resp.name}"`);

    resp.columns.unshift("id");
    resp.columns.push("Actions");
    resp.columns.forEach(col => {
        const th = $("<th></th>");
        th.html(col.columnName);
        header.append(th);
    });
    // post process each row
    let rows = [];
    for (const rid in resp.rows) {
        resp.rows[rid].unshift(rid);
        resp.rows[rid] = resp.rows[rid].map((cell, idx) => idx === 0 ? cell : `<input type="text" class="form-control form-control-sm" value="${cell}">`);
        resp.rows[rid].push(`<a href="#"><i class="fas fa-trash" onclick="window.deleteRow(this)"></i></a>
                             <a href="#"><i class="fas fa-plus" onclick="window.addRow(this)"></i></a>`);
        rows.push(resp.rows[rid]);
    }
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

window.deleteRow = function(i) {
    const table = $('#table').DataTable();
    table.row($(i).parents('tr')).remove().draw();
}

window.addRow = function(i) {
    const table = $('#table').DataTable();
    table.row.add([]).draw();
}

window.save = function() {
    console.log("Saving things right now!!");
}

window.stats = function () {
    const params = parseParam("id", "/tables");
    const id = parseInt(params.id);
    window.location = `summary?id=${id}`
}

$(document).ready(() => ready());
