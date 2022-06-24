// check for valid token before proceeding to main

drawTable = (resp) => {
    const header = $("thead tr")
    $("#table-name").html(`"${resp.name}"`);

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

    $('#dataTable').DataTable({
         bProcessing: true,
         paging: true,
         data: rows
    });
}


fetchTable = async (tid) => {
    const data = await ajax({url: `http://table.ulake.sontg.net/api/table/${tid}/data`});
    // console.log(data);
    if (!data && data.code !== 200 || !data.resp.columns || !data.resp.rows) {
        console.log(`Error fetching table ${tid}`);
    }
    else {
        drawTable(data.resp);
    }
}

let urlParams = new URLSearchParams(location.search);
let tid = parseInt(urlParams.get("tid"));
fetchTable(tid);

