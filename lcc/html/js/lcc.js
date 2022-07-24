$(document).ready(() => {
    initNavbar();
    routeReady();
});

function routeReady() {
	const ready = {
        "^/$": indexReady,
        "^/dataset": datasetReady,
        "^/patients": patientReady,
		"^/list": listReady,
        "^/team": teamReady,
        "^/detect": detectReady,
        "^/classify": classifyReady,
		"^/cache": cacheReady
	};
	Object.keys(ready).forEach(r => {
	    if (new RegExp(r).test(window.location.pathname)) ready[r]();
	});
}

function initNavbar() {
	$("span.user-name").text(getUserName() || "Xin chào!!");
}

// index functions
function indexReady() {
}

// dataset functions
function datasetReady() {
}

// patients functions
function patientReady() {
	window.setTimeout(function () {
		$.ajax({
			url: "/data/patients3cm",
			success: data => {
				const patients = $.csv.toObjects(data.replace(/^\s*[\r\n]/gm, ""));
				if (Array.isArray(patients) && patients.length > 0) {
					const t = document.querySelector("tbody");
					while (t.firstChild) t.removeChild(t.firstChild);
					patients.forEach(p => {
						t.appendChild($(tplPatientRow(p))[0]);
					});
				}
				$("h6 i.fa-spinner").remove();
			}
	})
	}, 500);
}

// list image functions
function listReady() {
	window.setTimeout(function () {
		$.ajax({
			url: "/data/patients3cm",
			success: data => {
				const patients = $.csv.toObjects(data.replace(/^\s*[\r\n]/gm, ""));
				if (Array.isArray(patients) && patients.length > 0) {
					const t = document.querySelector("tbody");
					while (t.firstChild) t.removeChild(t.firstChild);
					patients.forEach(p => {
						t.appendChild($(tplListRow(p))[0]);
					});
				}
				$("h6 i.fa-spinner").remove();
			}
	})
	}, 500);
}

// detect functions
function detectReady() {
	const hrefs = window.location.href.split("/");
	const detectPos = hrefs.indexOf("detect");
	if (detectPos >= 0 && detectPos < hrefs.length - 1) {
		const id = hrefs[detectPos + 1];
		id = id.replace(/\.20/, "17");
		id = id.replace(/\.21/, "18");
		$.ajax({
			url: `/service/detect/${id}`,
			success: (data) => detectResultReady(data, id)
		});
	}
}

function detectResultReady(data, id) {
	if (typeof data === "string") data = JSON.parse(data);
	if (!data.pre_results || !Array.isArray(data.pre_results)) return;
	const result = document.querySelector("#result");
	while (result.firstChild) result.removeChild(result.firstChild);
	$("i.fa-spinner").remove()
	let idx = 0;
	data.pre_results.forEach(candidate => {
		let c = {
		    idx: ++idx,
		    slice: candidate[0],
            confidence: candidate[1].toFixed(2),
            data: candidate[2],
        };
		result.appendChild($(tplDetectResultRow(c))[0]);
	});

	const summary = $("#summary");
	summary.removeClass("invisible");
    $("span[data-bind=name]").text(id);
	$("span[data-bind=count]").text(data.pre_results.length);
}

// classify functions
function classifyReady() {
	const hrefs = window.location.href.split("/");
	const detectPos = hrefs.indexOf("classify");
	if (detectPos >= 0 && detectPos < hrefs.length - 1) {
		const id = hrefs[detectPos + 1];
		id = id.replace(/\.20/, "17");
		id = id.replace(/\.21/, "18");
		setTimeout(() => {
			$.ajax({
				url: `/service/classify/${id}`,
				success: (data) => classifyResultReady(data, id)
			})}, 6000);
	}
}

function classifyResultReady(data, id) {
	if (!data.pre_results || !Array.isArray(data.pre_results)) return;
	const result = document.querySelector("#result");
	while (result.firstChild) result.removeChild(result.firstChild);
	let idx = 0;
	data.pre_results.forEach(candidate => {
		let c = {
		    idx: ++idx,
		    slice: candidate[0],
            confidence: candidate[1].toFixed(2),
            data: candidate[2],
        };
		result.appendChild($(tplClassifyResultRow(c))[0]);
	});

	const summary = $("#summary");
	summary.removeClass("invisible");
    $.bindings({
		count: data.pre_results.length,
		name: id
    });
}


// contact functions
function teamReady() {
}


let cacheId = [];
function cacheReady() {
	$.ajax({
		url: "/data/patients3cm",
		success: data => {
			const patients = $.csv.toObjects(data.replace(/^\s*[\r\n]/gm, ""));
			if (Array.isArray(patients) && patients.length > 0) {
				patients.forEach(p => cacheId.push(p.ID) );
				cacheId.slice(3);
			}
		}
	});
}

function cacheLastId() {
	if (cacheId.length > 0) {
		let id = cacheId.pop();
		console.log(`caching classify ${id}`);
		$.ajax({
			url: `/service/classify/${id}`,
			success: data => {
				console.log(`caching detect ${id}`);
				$.ajax({
					url: `/service/detect/${id}`,
					success: data => {
						cacheLastId();
					}
				})
			}
		})
	}
}
