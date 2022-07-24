import { BaseModal } from "../modal/base.js";

export class RenameModal extends BaseModal {
    constructor(callback) {
        super(callback, "#rename-modal");
        const _baseModal = this;
        this.name = this.body.find("#name");
        this.modal.on("show.bs.modal", () => this.name.val(this.oldName));
        this.modal.on("shown.bs.modal", () => this.name.focus());
        this.footer.find(".btn-primary").off("click").on("click", () => this.callback(this.name.val()));
    }
}
