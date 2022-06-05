// statistics functions

stats = {
    userSettings: {
        type: 'line',
        data: {
            datasets: [{
                label: "New users",
                lineTension: 0.3,
                backgroundColor: "rgba(78, 115, 223, 0.05)",
                borderColor: "rgba(78, 115, 223, 1)",
                pointRadius: 3,
                pointBackgroundColor: "rgba(78, 115, 223, 1)",
                pointBorderColor: "rgba(78, 115, 223, 1)",
                pointHoverRadius: 3,
                pointHoverBackgroundColor: "rgba(78, 115, 223, 1)",
                pointHoverBorderColor: "rgba(78, 115, 223, 1)",
                pointHitRadius: 10,
                pointBorderWidth: 2
            }],
        },
        options: {
            maintainAspectRatio: false,
            layout: {
                left: 10,
                right: 25,
                top: 25,
                bottom: 0
            }
        },
        scales: {
            xAxes: [{
                time: {
                    unit: 'date'
                },
                gridLines: {
                    display: false,
                    drawBorder: false
                },
                ticks: {
                    maxTicksLimit: 7
                }
            }],
            yAxes: [{
                ticks: {
                    maxTicksLimit: 5,
                    padding: 10,
                    // Include a dollar sign in the ticks
                    // callback: function (value, index, values) {
                    //     return '$' + number_format(value);
                    // }
                },
                gridLines: {
                    color: "rgb(234, 236, 244)",
                    zeroLineColor: "rgb(234, 236, 244)",
                    drawBorder: false,
                    borderDash: [2],
                    zeroLineBorderDash: [2]
                }
            }],
        },
        legend: {
            display: false
        },
        tooltips: {
            backgroundColor: "rgb(255,255,255)",
            bodyFontColor: "#858796",
            titleMarginBottom: 10,
            titleFontColor: '#6e707e',
            titleFontSize: 14,
            borderColor: '#dddfeb',
            borderWidth: 1,
            xPadding: 15,
            yPadding: 15,
            displayColors: false,
            intersect: false,
            mode: 'index',
            caretPadding: 10,
            // callbacks: {
            //     label: function (tooltipItem, chart) {
            //         var datasetLabel = chart.datasets[tooltipItem.datasetIndex].label || '';
            //         return datasetLabel + ': $' + number_format(tooltipItem.yLabel);
            //     }
            // }
        }
    },

    coreSettings: {
        type: 'pie',
        options: {
            maintainAspectRatio: false
        },
        data: {
            datasets: [{
                label: "Storage usage",
                backgroundColor: ["#0074D9", "#FF4136", "#2ECC40", "#FF851B", "#7FDBFF", "#B10DC9", "#FFDC00", "#001f3f", "#39CCCC", "#01FF70", "#85144b", "#F012BE", "#3D9970", "#111111", "#AAAAAA"]
            }],
        }
    },

    updateStats: async () => {
        const userStats = await ajax({url: "/api/admin/users/stats"});
        if (userStats && userStats.code === 200) {
            console.log(userStats);
            stats.redrawUserStats(userStats);
        }

        const coreStats = await ajax({url: "/api/admin/objects/stats"});
        if (coreStats && coreStats.code === 200) {
            console.log(coreStats);
            stats.redrawCoreStats(coreStats);
        }

        const folderStats = await ajax({url: "/api/admin/folders/stats"});
        if (folderStats && folderStats.code === 200) {
            console.log(folderStats);
            stats.redrawFolderStats(folderStats);
        }

        const fileStats = await ajax({url: "/api/admin/files/stats"});
        if (fileStats && fileStats.code === 200) {
            console.log(fileStats);
            stats.redrawFileStats(fileStats);
        }

    },

    redrawUserStats: (data) => {
        const ctx = document.getElementById("userStatChart");
        const chart = structuredClone(stats.userSettings);
        chart.data.labels = Object.keys(data.resp.regs);
        chart.data.datasets[0].data = Object.values(data.resp.regs);
        document.querySelector("#user-footer").textContent = `Total ${data.resp.count} users`
        new Chart(ctx, chart);
    },

    redrawCoreStats: (data) => {
        const ctx = document.getElementById("coreStatChart");
        const chart = structuredClone(stats.coreSettings);
        chart.data.labels = [ "Used storage", "Remaining" ];
        chart.data.datasets[0].data = [ parseInt(data.resp.stats.presentCapacity / 1048576), parseInt(data.resp.stats.capacity / 1048576) ];
        document.querySelector("#core-footer").textContent = `Total capacity ${parseInt(data.resp.stats.capacity / 1072147864)} GB`;
        new Chart(ctx, chart);
    },

    redrawFolderStats: (data) => {
        const ctx = document.getElementById("folderStatChart");
        const chart = structuredClone(stats.userSettings);
        chart.data.labels = Object.keys(data.resp.newFolders);
        chart.data.datasets[0].data = Object.values(data.resp.newFolders);
        chart.data.datasets[0].label = "New folders per day";
        document.querySelector("#folder-footer").textContent = `Total ${data.resp.count} folders`
        new Chart(ctx, chart);
    },

    redrawFileStats: (data) => {
        const ctx = document.getElementById("fileStatChart");
        const chart = structuredClone(stats.userSettings);
        chart.data.labels = Object.keys(data.resp.newFiles);
        chart.data.datasets[0].data = Object.values(data.resp.newFiles);
        chart.data.datasets[0].label = "New files per day";
        document.querySelector("#file-footer").textContent = `Total ${data.resp.count} files`
        new Chart(ctx, chart);
    },
}
