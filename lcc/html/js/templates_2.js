const tplDetectResultRow = (c) => `
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
                    Ứng viên số ${c.idx}
                    <br/>
                    Vị trí : lát cắt thứ ${c.slice}
                    <br/>
                    Độ tin cậy : ${c.confidence}
                    <br/>
                    <img src="data:image/png;base64,${c.data}" class="full-width"/>
                </div>
            </div>
        </div>
    </div>
`;

const tplPatientRow = (p) => `
    <tr>
        <td>${p.ID}</td>
        <td>${p.Patient_Name}</td>
        <td>${p.Sex}</td>
        <td>${p.Modality}</td>
        <td>${p.StudyDate}</td>
        <td><a class="btn btn-sm btn-primary" href="/detect/${p.Patient_Name}">Chẩn đoán</a></td>
    </tr>
`;
