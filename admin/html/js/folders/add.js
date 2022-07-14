import { AddModal } from "../addModal/modal.js";

export class AddFolderFileModal extends AddModal {
    constructor(callback) {
        super(callback);
        this.header.text("Add folder or file");
        this.folderName = this.body.find("#name");
        this.modal.on("show.bs.modal", () => this.folderName.val(""));
        this.modal.on("shown.bs.modal", () => this.folderName.focus());
        this.footer.find(".btn-primary").off("click").on("click", () => callback(this.folderName.val()));
    }
}
