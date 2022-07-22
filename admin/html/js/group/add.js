import { AddModal } from "../modal/add.js";

export class AddGroupModal extends AddModal {
    constructor(callback) {
        super(callback);
        this.modal.on("show.bs.modal", () => this.body.find("#name").val(""));
        this.modal.on("shown.bs.modal", () => this.body.find("#name").focus());
    }
}
