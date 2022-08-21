import { tableApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";
import { defaultLineChartSettings } from "../chartsettings.js";

function dropdownClicked(a) {
    const btnId = $(a).parents(".dropdown-menu").attr("aria-labelledby");
    $("#" + btnId).html($(a).text()+' <span class="caret"></span>');
}

class SummaryCRUD {
    xaxis = ["time", "date", "datetime", "timestamp", "thời gian", "ngày", "ngày giờ"];

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

    // draw table from prepared rows
    drawTable(resp, tableRows) {
        const header = $("thead tr")
        $("#name-detail").html(`Summary for table "${resp.name}"`);

        // make columns
        resp.columns.forEach(col => {
            const th = $("<th></th>");
            th.html(col.columnName);
            header.append(th);
        });

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

    // standardize year, month, day format
    // output: yyyy-MM-dd hh:mm
    normalizeDateTime(rows, timeColIndex) {
        const normalizers = {
            "^(\\d{4})-(\\d{1,2})-(\\d{1,2}) (\\d{1,2}):(\\d{1,2})$": "$1-$2-$3 $4:$5",
            "^(\\d{4})$": "$1-01-01 00:00",                             // yyyy
            "^(\\d{1,2})[-/](\\d{4})$": "$2-$1-01 00:00",               // mm/yyyy
            "^(\\d{1,2})[-/](\\d{1,2})[-/](\\d{4})$": "$3-$1-$2 00:00", // mm/dd/yyyy
        };
        rows.forEach(row => {
            let label = row[timeColIndex];
            for (const [norm, value] of Object.entries(normalizers)) {
                const re = new RegExp(norm);
                if (label.match(re)) {
                    label = label.replace(re, value);
                    // post processing: pad with zeros
                    label = label.replace(/\d+/g, m => "0".substr(m.length - 1) + m);
                    row[timeColIndex] = label;
                    console.log(`matched: ${label} with ${norm}, replace to ${row[timeColIndex]}`);
                    break;
                }
            }
        });
        return rows;
    }

    // sort rows by time column
    sort(rows, timeColIndex) {
        const labels = rows.map(r => r[timeColIndex]);
        rows = this.normalizeDateTime(rows, timeColIndex);
        rows = rows.sort((a, b) => {
            //console.log(`comparing ${a[timeColIndex]} and ${b[timeColIndex]}`);
            if (a[timeColIndex] < b[timeColIndex]) return -1;
            if (a[timeColIndex] > b[timeColIndex]) return 1;
            return 0;
        });
        console.log(rows);
        return rows;

    }

    drawChart() {
        const group = $("#groupDropdownButton").text().trim();
        const field = $("#fieldDropdownButton").text().trim();
        // console.log(`Draw chart for group ${group}, and field ${field}`);
        if (group === "Group" || field === "Field") return;
        const groupColIdx = this.getGroupColIndices(this.data.columns);
        let rows = [];

        // flatten object keys in selected group into array
        for (const rid in this.data.rows) {
            if (this.data.rows[rid][groupColIdx] === group)
                rows.push(this.data.rows[rid]);
        }

        // find time/date/date time/timestamp column for labelss
        const timeColIndex = this.data.columns
                    .map(c => c.columnName.toLowerCase())
                    .findIndex(c => this.xaxis.includes(c));
        // console.log("time col idx", timeColIndex);
        if (timeColIndex < 0) timeColIndex = 0; // default first column

        const dataColIndex = this.data.columns.findIndex(c => c.columnName === field);
        if (dataColIndex < 0) dataColIndex = 0; // default first column


        rows = this.sort(rows, timeColIndex);

        const ctx = $("#graph");
        const chart = structuredClone(defaultLineChartSettings);
        chart.data.labels = rows.map(r => r[timeColIndex]);
        chart.data.datasets[0].data = rows.map(r => r[dataColIndex]);
        chart.data.datasets[0].label = field;
        if (this.chart) this.chart.destroy();
        this.chart = new Chart(ctx, chart);
        // console.log(rows);
    }

    genSelects(resp, summary) {
        for (const key in summary) {
            const item = $(`<a class="dropdown-item" href="#">${key}</a>`)
            item.click(function () { dropdownClicked(this); window.crud.drawChart();});
            $("#groupDropdownList").append(item);
        }

        // make field column
        resp.columns.forEach(col => {
            const item = $(`<a class="dropdown-item" href="#">${col.columnName}</a>`)
            item.click(function () { dropdownClicked(this); window.crud.drawChart();});
            $("#fieldDropdownList").append(item);
        });
    }

    async ready() {
        const params = parseParam("id", "/tables");
        const id = parseInt(params.id);
        this.data = await tableApi.data(id);        ;
        const tableRows = this.genSummaryRows(this.data.rows, this.data.columns);
        this.drawTable(this.data, tableRows);
        this.genSelects(this.data, this.summary);
    }
}

window.crud = new SummaryCRUD();
$(document).ready(() => window.crud.ready());
