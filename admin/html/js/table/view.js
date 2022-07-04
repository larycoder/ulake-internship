// SSI CRUD: <!--# include virtual="/js/crud.js" -->

drawTable = (resp) => {
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
         rows.push(resp.rows[rid]);
    }
    console.log(rows);

    $('#table').DataTable({
         bProcessing: true,
         paging: true,
         data: rows
    });
}

async function tableReady() {
    const params = parseParam("id", "/tables");
    const id = parseInt(params.id);
    const data = await tableApi.data(id);
    drawTable(data);
}

$(document).ready(() => tableReady());