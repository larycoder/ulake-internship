import { BaseModal } from "../modal/base.js";

export class AddGroupModal extends BaseModal {
    constructor(callback) {
        super(callback);
        this.modal.on("show.bs.modal", () => this.body.find("#name").val(""));
        this.modal.on("shown.bs.modal", () => this.body.find("#name").focus());
    }
}
