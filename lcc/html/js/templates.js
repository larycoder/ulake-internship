const tplDetectResultRow = (d) => `
    <div class="row pb-3">
        <div class="col-md-12">
            <div class="row">
                <div class="mx-auto"></div>
                <div class="col-md-6">
                    <hr/>
                </div>
                <div class="mx-auto"></div>
            </div>

            <div class="row pb-5">
                <div class="col-md-12">
                    Ứng viên số ${d.idx}
                    <br/>
                    Vị trí : lát cắt thứ ${d.slice}
                    <br/>
                    <img src="data:image/png;base64,${d.data}" class="full-width"/>
                    <br/>
                    <input type="checkbox"><label>"Đánh dấu nếu đồng thuận."</label>
                </div>
            </div>
        </div>
    </div>
`;

const tplClassifyResultRow = (d) => `
    <div class="row pb-3">
        <div class="col-md-12">
            <div class="row">
                <div class="mx-auto"></div>
                <div class="col-md-6">
                    <hr/>
                </div>
                <div class="mx-auto"></div>
            </div>

            <div class="row pb-5">
                <div class="col-md-12">
                    Ứng viên số ${d.idx}
                    <br/>
                    Vị trí : lát cắt thứ ${d.slice}
                    <br/>
                    Xác suất ác tính : ${d.confidence} %
                    <br/>
                    <img src="data:image/png;base64,${d.data}" class="full-width"/>
                    <br/>
                    <input type="checkbox"><label>"Đánh dấu nếu đồng thuận."</label>
                </div>
            </div>
        </div>
    </div>
`;

const tplPatientRow = (p) => `
    <tr>
        <td>${p.ID}</td>
        <td>${p.ID}</td>
        <td>${p.Sex}</td>
        <td>${p.Modality}</td>
        <td>${p.StudyDate}</td>
        <td><a class="btn btn-sm btn-primary" href="/detect/${p.ID}">Định vị khối u</a></td>
    </tr>
`;

const tplListRow = (p) => `
    <tr>
        <td>${p.ID}</td>
        <td>${p.ID}</td>
        <td>${p.Sex}</td>
        <td>${p.Modality}</td>
        <td>${p.StudyDate}</td>
    </tr>
`;
