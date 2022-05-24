/**
 * query page controller
 */

/**
 * Singleton object must be implemented
 */
var table; // table controller
var filterChip; // filter controller
var fs; // file system controller


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
                return createActionColumn();
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
    // download
    let openBtn = $(row).find("#open")[0];
    let func = undefined;
    if (data.fileType === "file") {
        func = "downloadFile(\"" + data.cid + "\")";
    } else {
        func = "loadFolder(" + JSON.stringify(data) + ")";
    }
    openBtn.setAttribute("onclick", func);
}


/**
 * load data from folder model
 * @param {Object} folder
 */
function loadFolder(folder) {
    fs.moveIn(folder);
    searchData(table);
}


/**
 * move back 1 level of folder
 */
function leaveFolder() {
    fs.moveOut(1);
    searchData(table);
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
 * upload file to lake
 * @param {Object} event on change event
 */
function uploadFile(event) {
    let progressBar = new ProgressController("progress-modal");
    let file = event.target.files[0];
    let fileInfo = new FileModel();
    fileInfo.name = file.name;
    fileInfo.mime = file.type;
    fileInfo.size = file.size;

    progressBar.start();
    let client = new ULakeQueryClient();
    client.uploadFile(fileInfo, file, (resp) => {
        console.log(resp);
        progressBar.end();
    });
    progressBar.waitUntilEnd();
}


/**
 * list of action button
 */
function createActionColumn() {
    // container
    let container = document.createElement("div");

    // download
    let downBtn = document.createElement("button");
    downBtn.setAttribute("class", "btn btn-dark");
    downBtn.setAttribute("id", "open");
    downBtn.innerHTML = "open";
    container.appendChild(downBtn);

    return container.outerHTML;
}

/**
 * represent data list in table
 * @param {TableController} table
 * @param {Object} data data of type list
 */
function redrawTable(data, table) {
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
};


/**
 * query data from server and return back to query-table-result
 * compatible to query_page.html
 */
function searchData(table) {
    let ulake = new ULakeQueryClient();
    let progressBar = new ProgressController("progress-modal");

    // collect user filter
    let filterList = filterChip.getChipValues();

    // data represent
    folderRepr = (code, folder) => {
        let dataList = [];
        for (let sub of folder.subFolders) {
            dataList.push({
                id: sub.id,
                name: sub.name,
                ownerId: sub.ownerId,
                size: "",
                cid: "",
                fileType: "folder"
            });
        }
        for (let file of folder.files) {
            dataList.push({
                id: file.id,
                name: file.name,
                ownerId: file.ownerId,
                size: file.size,
                cid: file.cid,
                fileType: "file"
            });
        }
        let fakeResp = {
            code: code,
            resp: dataList
        }
        redrawTable(new DataListModel(fakeResp), table);
        progressBar.end();
    }

    rootRepr = (root) => {
        root.head.push("fileType");
        for (let f of root.data) {
            f["fileType"] = "folder";
        }
        redrawTable(root, table);
        progressBar.end();
    }

    /* start collecting data */
    progressBar.start();
    if (fs.size() > 0) {
        let folderId = fs.get(fs.size() - 1).id;
        ulake.getFolderEntries(folderRepr, folderId, filterList);
    } else {
        ulake.getRoot(rootRepr, filterList);
    }

    /* done process */
    progressBar.waitUntilEnd();
    redrawFS();
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
    listProp = globalObject.properties["folder"];

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


/**
 * redraw current path to page
 */
function redrawFS() {
    let el = document.getElementById("file-path");
    el.innerHTML = fs.toString();
}
