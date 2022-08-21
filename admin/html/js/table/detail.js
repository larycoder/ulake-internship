import { tableApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

class TableDetailCRUD {
    drawTable(resp) {
        const header = $("#table thead tr")
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
            resp.rows[rid].push(`<a href="#"><i class="fas fa-trash" onclick="window.crud.deleteRow(this)"></i></a>
                                <a href="#"><i class="fas fa-plus" onclick="window.crud.addRow(this)"></i></a>`);
            rows.push(resp.rows[rid]);
        }
        $.fn.dataTable.ext.errMode = 'none';
        $('#table').DataTable({
            bProcessing: true,
            paging: true,
            scrollX: true,
            ordering: false,
            data: rows
        });
    }

    async ready() {
        const params = parseParam("id", "/tables");
        const id = parseInt(params.id);
        const data = await tableApi.data(id);
        this.drawTable(data);
    }

    deleteRow(i) {
        const table = $('#table').DataTable();
        table.row($(i).parents('tr')).remove().draw();
    }

    addRow(i) {
        const table = $('#table').DataTable();
        table.row.add([]).draw();
    }

    save() {
        console.log("Saving things right now!!");
    }

    saveColumn() {
        console.log("Saving things right now!!");
    }

    stats() {
        const params = parseParam("id", "/tables");
        const id = parseInt(params.id);
        window.location = `summary?id=${id}`
    }
}

window.crud = new TableDetailCRUD();
$(document).ready(() => window.crud.ready());
