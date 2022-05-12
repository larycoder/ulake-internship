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
            if (column === "action") { // download action
                let btn = document.createElement("button");

                btn.setAttribute("class", "btn btn-dark");
                btn.setAttribute("id", "download");
                btn.innerHTML = "Button";

                return btn.outerHTML;
            } else {
                return data;
            }
        },
        defaultContent: undefined
    };
}

/**
 * decorate each row in data table
 * @param {Node} row row node of table in datatable
 * @param {Object} data original data passed to row
 */
function decorateRow(row, data) {
    let downloadBtn = $(row).find("#download")[0];
    let func = "downloadFile(\"" + data.cid + "\")";
    downloadBtn.setAttribute("onclick", func);
}

/**
 * download file function
 * @param {String} cid id of file object
 */
function downloadFile(cid) {
    let client = new ULakeQueryClient();
    client.getObjectData(cid);
};

/**
 * query data from server and return back to query-table-result
 * compatible to query_page.html
 */
function searchData(table) {
    let ulake = new ULakeQueryClient();
    let progressBar = new ProgressController("progress-modal");
    let option = document.getElementById("query-data-list").value;

    // collect user filter
    let filterList = filterChip.getChipValues();

    let dataRepr = (data) => {
        if (data.getCode() == 200) {
            table.clear();

            // decorate head
            headers = [];
            for (let head of data.getHead()) {
                headers.push(decorateColumn(head));
            }

            // action column
            headers.push(decorateColumn("action"));

            table.writeHead(headers, decorateRow);
            table.addData(data.getAllData());
        } else {
            console.log("error resp: ");
            console.log(data);
            table.clear();
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
        table.clear();
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
    closeModal("filter-modal");
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

    openModal("filter-modal");
}
