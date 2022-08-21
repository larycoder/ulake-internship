import { adminApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js"
import { defaultLineChartSettings, defaultPieChartSettings } from "./chartsettings.js";

// statistics functions

class Stats {
    constructor() {
        this.statTypes = [
            {
                api: adminApi.userStats,
                draw: this.drawUserStats,
            },
            {
                api: adminApi.coreStats,
                draw: this.drawCoreStats,
            },
            {
                api: adminApi.folderStats,
                draw: this.drawFolderStats,
            },
            {
                api: adminApi.fileStats,
                draw: this.drawFileStats,
            },
        ];
    }

    async updateStats() {
        this.statTypes.forEach(async s => {
            const statsData = await s.api.call(adminApi);
            if (statsData) {
                console.log(statsData);
                s.draw(statsData);
            }
        })
    }

    drawUserStats(data) {
        const ctx = $("#userStatChart");
        const chart = structuredClone(defaultLineChartSettings);
        chart.data.labels = Object.keys(data.regs);
        chart.data.datasets[0].data = Object.values(data.regs);
        chart.data.datasets[0].label = "New users";
        $("#user-footer").text(`Total ${data.count} users`);
        new Chart(ctx, chart);
    }

    drawCoreStats(data) {
        const ctx = $("#coreStatChart");
        const chart = structuredClone(defaultPieChartSettings);
        chart.data.labels = [ "Used storage", "Remaining" ];
        chart.data.datasets[0].data = [ parseInt(data.stats.presentCapacity / 1048576), parseInt(data.stats.capacity / 1048576) ];
        chart.data.datasets[0].label = "Storage usage";
        $("#core-footer").text("").append($(`<span>Total capacity ${parseInt(data.stats.capacity / 1072147864)} GB <br/>Data replication: ${parseInt(data.stats.replication)} times.</span>`));
        new Chart(ctx, chart);
    }

    drawFolderStats(data) {
        const ctx = $("#folderStatChart");
        const chart = structuredClone(defaultLineChartSettings);
        chart.data.labels = Object.keys(data.newFolders);
        chart.data.datasets[0].data = Object.values(data.newFolders);
        chart.data.datasets[0].label = "New folders per day";
        $("#folder-footer").text(`Total ${data.count} folders`);
        new Chart(ctx, chart);
    }

    drawFileStats(data) {
        const ctx = $("#fileStatChart");
        const chart = structuredClone(defaultLineChartSettings);
        chart.data.labels = Object.keys(data.newFiles);
        chart.data.datasets[0].data = Object.values(data.newFiles);
        chart.data.datasets[0].label = "New files per day";
        $("#file-footer").text(`Total ${data.count} files`)
        new Chart(ctx, chart);
    }
}

const stats = new Stats();
$(document).ready(() => stats.updateStats());
