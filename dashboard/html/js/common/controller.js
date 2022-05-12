/**
 * controller classes for DOM components
 */

/**
 * table class which is used to control dynamically table in DOM
 */
class TableController {
    /**
     * controller use table ID in DOM to map controller to DOM instance
     * @param {String} id - DOM table ID
     */
    constructor(id) {
        this.id = id;
        this.instance = document.getElementById(id);
        this.tableHead = this.instance.tHead;
        this.tableBodies = this.instance.tBodies;
    }

    /**
     * initialize magic table
     */
    start() {
        this.api = $("#" + this.id).DataTable();
    }

    /**
     * initialize magic table with column name
     * @param {Array<Object>} columns list of header
     * @param {Function} rowCallback callback function for each row
     */
    start(columns, rowCallback) {
        this.api = $("#" + this.id).DataTable({
            columns: columns,
            rowCallback: rowCallback
        });
    }

    /**
     * private method for fast creation of DOM element
     * @param {String} name - element name
     */
    #newElement(name) {
        return document.createElement(name);
    }

    /**
     * clear current table head if exist and update new head
     * @param {Array.<Object>} headList headers for DataTable columns parameter
     * @param {Function} callback(row, data) decorator for each row
     */
    writeHead(headList, callback) {
        // update new header
        this.tableHead.innerHTML = "";
        let tr = this.#newElement("tr");
        for (let head of headList) {
            let th = this.#newElement("th");
            th.scope = "col";
            th.innerText = head["data"];
            tr.appendChild(th);
        }
        this.tableHead.appendChild(tr);

        // restart table instance
        this.start(headList, callback);
    }

    /**
     * clear all row in first table bodies
     */
    clearData() {
        this.api.clear().draw();
    }

    /**
     * append new row to first table bodies
     * @param {Array.<String>} dataList - list of new elements in row
     */
    addData(dataList) {
        this.api.rows.add(dataList).draw();
    }

    /**
     * clear both header and all data
     */
    clear() {
        // destroy data table instance
        if (this.api != undefined) {
            this.api.destroy();
            this.api = undefined;
        }
        this.tableBodies[0].innerHTML = "";
        this.tableHead.innerHTML = "";
    }
}

/**
 * chip class used to control list of chip
 */
class ChipController {
    /**
     * constructor use DOM chip ID to map instance to DOM
     * @param {String} id - chip list id expecting in <ul> tag
     */
    constructor(id) {
        this.id = id;
        this.list = document.getElementById(id);
    }

    /**
     * clear all chip
     */
    clear() {
        this.list.innerHTML = "";
    }

    /**
     * create new element from name
     * @param {String} name - name of tag
     */
    #newElement(name) {
        return document.createElement(name);
    }

    /**
     * append new chip
     * @param {String} value - chip value
     */
    addChip(value) {
        let chipId = this.id + "-" + this.list.childElementCount;

        // build div
        let span = this.#newElement("span");
        span.classList.add("closebtn");
        span.innerHTML = "&times;";
        span.onclick = (_event) => {
            // delete itself when click x
            let el = document.getElementById(chipId);
            el.parentNode.removeChild(el);
        }

        let h8 = this.#newElement("h8");
        h8.innerHTML = value;

        // build chip
        let chip = this.#newElement("li");
        chip.style.display = "inline-block";
        chip.classList.add("chip");
        chip.classList.add("list-inline");
        chip.appendChild(h8);
        chip.appendChild(span);
        chip.id = chipId;
        this.list.appendChild(chip);
    }

    /**
     * return all chip value in current instance
     * @returns {Array.<String>} list of value
     */
    getChipValues() {
        let rst = [];
        for (let chip of this.list.children) {
            let h8 = chip.getElementsByTagName("H8")[0];
            rst.push(h8.innerText);
        }
        return rst;
    }
}

/**
 * progress bar used to control loading in a modal
 */
class ProgressController {
    /**
     * constructor use DOM Id to detect progress spinner instance
     * @param {String} modalId - Id of modal progress bar
     * @param {String} modalButtonId - Id of progress modal toggle button
     */
    constructor(modalId) {
        this.modalId = modalId;
        this.isRun = false;
    }

    /**
     * start spinner
     */
    start() {
        openModal(this.modalId);
        this.isRun = true;
    }

    /**
     * stop spinner
     */
    end() {
        this.isRun = false;
    }

    /**
     * wait until end to close spinner (minimum at 0.5s)
     */
    waitUntilEnd() {
        setTimeout(() => {
            if (this.isRun == true) {
                this.waitUntilEnd();
            } else {
                setTimeout(() => {
                    closeModal(this.modalId);
                }, 100);
            }
        }, 500);
    }
}
