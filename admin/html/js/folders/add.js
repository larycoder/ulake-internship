import { AddModal } from "../addModal/modal.js";

export class AddFolderFileModal extends AddModal {
    constructor(callback) {
        super(callback);
        this.header.text("Add folder or file");
        this.modal.on("show.bs.modal", () => this.body.find("#name").val(""));
        this.modal.on("shown.bs.modal", () => this.body.find("#name").focus());

    }
}
