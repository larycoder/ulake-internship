import { tableApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

class SummaryCRUD {
    // get group names. should be multiple columns but only one group for now.
    getCombineGroupCol(row, groupColIndices) {
        return groupColIndices
                    .map(colIdx => row[colIdx])
                    .join("-");
    }

    // get a list of column indices for grouping
    getGroupColIndices(cols) {
        let ret = [];
        for (let i = 0; i < cols.length; i++) {
            if (!!cols[i].groupBy) {
                ret.push(i);
            }
        }
        return ret;
    }

    // remove empty rows
    countTotalLength(row) {
        return row.reduce((prev, curr) => {
            if (typeof prev === 'string' || prev instanceof String) prev = prev.length;
            return prev + curr.length;
        });
    }

    // group rows into groups, as indicated by groupColIndices
    groupRows(rows, groupColIndices) {
        let ret = {};     // will be in structure { groupedItem: [rows] }
        for (const rid in rows) {
            if (this.countTotalLength(rows[rid]) > 0) {
                const key = this.getCombineGroupCol(rows[rid], groupColIndices);
                if (!ret.hasOwnProperty(key)) {
                    ret[key] = [];
                }
                ret[key].push(rows[rid]);
            }
        }
        return ret;
    }

    // for each group, we perform average, min, and max
    calcStats(groups) {
        let ret = {};
        for (const groupName in groups) {
            let statsKey = {min: [], max: [], avg: [], sum: []};
            for (const row of groups[groupName]) {
                for (let i = 0; i < row.length; i++) {
                    if (isNumeric(row[i])) {
                        const float = parseFloat(row[i]);
                        if (!statsKey.sum[i]) statsKey.sum[i] = 0;
                        if (!statsKey.min[i]) statsKey.min[i] = float;
                        if (!statsKey.max[i]) statsKey.max[i] = float;

                        if (statsKey.min[i] < float) statsKey.min[i] = float;
                        if (statsKey.max[i] > float) statsKey.max[i] = float;
                        statsKey.sum[i] += float;
                    }
                }
            }
            statsKey.min = statsKey.min.map(i => i ? +i.toFixed(2) : "");
            statsKey.max = statsKey.max.map(i => i ? +i.toFixed(2) : "");
            statsKey.avg = statsKey.sum.map(i => i ? +(i/groups[groupName].length).toFixed(2) : "");
            delete statsKey.sum;
            ret[groupName] = statsKey;
        }
        return ret;
    }

    // generate summary from stats
    summarize(stats, groupColIndices) {
        for (const groupName in stats) {
            // a row for one group
            const groupStats = stats[groupName];
            for (const statKey in groupStats)    // min, max, avg
                groupStats[statKey][groupColIndices[0]] = `${groupName}: ${statKey}`;
        }
        return stats;
    }

    // perform summary for groups
    genSummaryRows(rows, cols) {
        let tableRows = [];
        const groupColIndices = this.getGroupColIndices(cols);
        if (groupColIndices.length) {
            const groups = this.groupRows(rows, groupColIndices);
            const stats = this.calcStats(groups);
            this.summary = this.summarize(stats, groupColIndices);
            // convert this into table row
            for (const groupName in this.summary) {
                const groupStats = stats[groupName];
                for (const row in groupStats)
                    tableRows.push(groupStats[row]);
            }
        }
        else {
            // show all by default
            tableRows.push(resp.rows[rid]);
        }
        // console.log(tableRows);
        return tableRows;
    }

    drawTable(resp) {
        const header = $("thead tr")
        $("#name-detail").html(`Summary for environmental table "${resp.name}"`);

        // make columns
        resp.columns.forEach(col => {
            const th = $("<th></th>");
            th.html(col.columnName);
            header.append(th);
        });

        const tableRows = this.genSummaryRows(resp.rows, resp.columns);

        // post process each row
        $.fn.dataTable.ext.errMode = 'none';
        this.table = $('#table').DataTable({
            scrollX: true,
            bProcessing: true,
            searching: false,
            paging: false,
            ordering: false,
            buttons: [ 'csv' ],
            data: tableRows
        });
    }

    genSelects(summary) {

    }

    async ready() {
        const params = parseParam("id", "/tables");
        const id = parseInt(params.id);
        const data = await tableApi.data(id);
        $(".dropdown-menu li a").click(function(){
            $(".btn:first-child").html($(this).text()+' <span class="caret"></span>');
        });
        this.drawTable(data);
        this.genSelects(this.summary);
    }

}

window.crud = new SummaryCRUD();
$(document).ready(() => window.crud.ready());
