// TODO: refactor to BaseModal
export class AddModal {
    constructor(callback, modalId) {
        this.callback = callback;
        this.modal = $(modalId ? modalId : "#add-modal");
        this.header = this.modal.find(".modal-title");
        this.body = this.modal.find(".modal-body");
        this.footer = this.modal.find(".modal-footer");
        this.footer.find(".btn-primary").off("click").on("click", () => callback());
    }
}