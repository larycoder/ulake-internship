import { AddModal } from "../modal/add.js";

export class RenameModal extends AddModal {
    constructor(callback) {
        super(callback, "#rename-modal");
        const _addModal = this;
        this.name = this.body.find("#name");
        this.modal.on("show.bs.modal", () => this.name.val(this.oldName));
        this.modal.on("shown.bs.modal", () => this.name.focus());
        this.footer.find(".btn-primary").off("click").on("click", () => this.callback(this.name.val()));
    }
}
