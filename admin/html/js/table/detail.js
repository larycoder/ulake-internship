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
            resp.rows[rid] = resp.rows[rid].map((cell, idx) => idx === 0 ? cell : `<input type="text" class="form-control form-control-sm" value="${cell}" onblur="window.crud.focusLoss(this)">`);
            resp.rows[rid].push(`<a href="#"><i class="fas fa-trash" onclick="window.crud.deleteRow(this)"></i></a>
                                <a href="#"><i class="fas fa-plus" onclick="window.crud.addRow(this)"></i></a>`);
            rows.push(resp.rows[rid]);
        }
        $.fn.dataTable.ext.errMode = 'none';
        this.table = $('#table').DataTable({
            bProcessing: true,
            paging: true,
            scrollX: true,
            ordering: false,
            data: rows
        });

        this.columnTable = $('#column-table').DataTable({
            bProcessing: false,
            paging: false,
            ordering: false,
            searching: false,
            data: resp.columns.filter(c => !isString(c)),
            columns: [
                { data: "id" },
                { data: "columnName" },
                { data: "dataType",
                    render: (data, type, row) => `<select class="form-control" value="${data}" onchange="window.crud.columnSelectChange(this)">
                                                    <option value="number" ${data === "number" ? "selected" : ""}>number</option>
                                                    <option value="date" ${data === "date" ? "selected" : ""}>date</option>
                                                    <option value="lat" ${data === "date" ? "selected" : ""}>lattitude</option>
                                                    <option value="lon" ${data === "date" ? "selected" : ""}>longitude</option>
                                                    <option value="string" ${data === "string" ? "selected" : ""}>string</option>
                                                 </select>`
                },
                { data: "groupBy",
                    render: (data, type, row) => `<input type="checkbox" ${data === true? "checked" : ""} data-field="read" onchange="window.crud.columnCheckChange(this)">`
                }
            ]
        });
        console.log(resp.columns);
    }

    async ready() {
        const params = parseParam("id", "/tables");
        const id = parseInt(params.id);
        this.data = await tableApi.data(id);
        this.drawTable(this.data);
    }

    deleteRow(i) {
        this.table.row($(i).parents('tr')).remove().draw();
    }

    addRow(i) {
        this.table.row.add([]).draw();
    }

    save() {
        // TODO: console.log("Saving things right now!!");
        showToast("Info", "Saved table data");
    }

    saveColumn() {
        // TODO
        const columns = this.data.columns.filter(c => !isString(c));
        console.log("Saving things right now!!", columns);
        tableApi.saveColumn(columns);
        showToast("Info", "Saved columns");

    }

    columnCheckChange(button) {
        var $button = $(button);
        const row = $button.parents("tr");
        const data = this.columnTable.row(row).data();
        data.groupBy = $button.prop("checked");
    }

    columnSelectChange(button) {
        var $button = $(button);
        const row = $button.parents("tr");
        const data = this.columnTable.row(row).data();
        data.dataType = $button.val();
    }

    focusLoss(input) {
        this.lastInput = input;
    }

    fillDateNow() {
        if (!this.lastInput) return;
        const today = new Date();
        var hh = String(today.getHours()).padStart(2, '0');
        var mm = String(today.getMinutes()).padStart(2, '0');
        var dd = String(today.getDate()).padStart(2, '0');
        var MM = String(today.getMonth() + 1).padStart(2, '0'); //January is 0!
        var yyyy = today.getFullYear();
        this.lastInput.value = `${yyyy}-${MM}-${dd} ${hh}:${mm}`;
    }

    stats() {
        const params = parseParam("id", "/tables");
        const id = parseInt(params.id);
        window.location = `summary?id=${id}`;
    }
}

window.crud = new TableDetailCRUD();
$(document).ready(() => window.crud.ready());
