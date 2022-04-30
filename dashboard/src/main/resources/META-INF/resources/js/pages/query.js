/**
 * query page controller
 */

/**
 * decorate each column in data table
 * @param {String} column header name of each column
 * @returns {Object} object to push into DataTable initialization
 */
function decorateColumn(column) {
    return {
        data: column,
        render: (data) => {
            let cid = ULakeQueryClient.getDownloadLink(data);
            let rst;

            if (column == "cid") {
                rst = document.createElement("a");
                rst.href = cid;
            } else {
                rst = document.createElement("div");
            }

            rst.innerHTML = data;
            rst.id = column;
            return rst.outerHTML;
        }
    };

}

/**
 * decorate each row in data table
 * @param {Node} row row node of table in datatable
 * @param {Object} data original data passed to row
 */
function decorateRow(row, data) {
    let cids = $(row).find("#cid");
    for(let cid of cids) {
        // build download file name
        let file = "";
        if("name" in data) file += "_" + data.name;
        if("mime" in data) file += "." + data.mime;
        file = data.cid + file;

        cid.download = file;
    }
}

/**
 * query data from server and return back to query-table-result
 * compatible to query_page.html
 */
function search() {
    let ulake = new ULakeQueryClient();
    let progressBar = new ProgressController("progress-modal-button");
    let option = document.getElementById("query-data-list").value;

    // collect user filter
    let filterList = filterChip.getChipValues();

    let dataRepr = (data) => {
        if (data.getCode() == 200) {
            resultTable.clear();

            // decorate head
            headers = [];
            for (let head of data.getHead()) {
                headers.push(decorateColumn(head));
            }

            resultTable.writeHead(headers, decorateRow);
            resultTable.addData(data.getAllData());
        } else {
            console.log("error resp: ");
            console.log(data);
            resultTable.clear();
        }
        progressBar.end();
    };

    /* start collecting data */
    progressBar.start();

    if (option == "object")
        ulake.getObjectList(dataRepr, filterList);
    else if (option == "file")
        ulake.getFileList(dataRepr, filterList);
    else {
        console.log(option + " data type is not supported");
        resultTable.clear();
        progressBar.end();
    }

    /* done process */
    progressBar.waitUntilEnd();
}

/**
 * add new filter chip to chip controller
 * note: this function is compatible with filter-modal in
 * query_page view
 */
function addFilterChip() {
    let property = document.getElementById("filter-modal-property").value;
    let operator = document.getElementById("filter-modal-operator").value;
    let value = document.getElementById("filter-modal-value").value;
    let filterString = property + " " + operator + " " + value;
    filterChip.addChip(filterString);
    document.getElementById("filter-modal-close").click();
}

/**
 * update property and operator for filter
 * compatible to filter-modal in query_page view
 */
function loadFilterOptions() {
    let property = document.getElementById("filter-modal-property");
    let operator = document.getElementById("filter-modal-operator");

    // detect properties list
    let type = document.getElementById("query-data-list").value;
    listProp = globalObject.properties[type];

    // update filter option
    operator.innerHTML = "";
    for (let value of globalObject.operators) {
        let opt = document.createElement('option');
        opt.value = value;
        opt.innerHTML = value;
        operator.appendChild(opt);
    }

    property.innerHTML = "";
    for (let value of listProp) {
        let opt = document.createElement('option');
        opt.value = value;
        opt.innerHTML = value;
        property.appendChild(opt);
    }
}
