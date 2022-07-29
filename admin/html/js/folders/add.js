import { BaseModal } from "../modal/base.js";

export class AddFolderFileModal extends BaseModal {
    constructor(callback) {
        super(callback);
        const _baseModal = this;
        this.header.text("Add folder or file");
        this.folderName = this.body.find("#name");
        this.modal.on("shown.bs.modal", () => this.folderName.focus());
        this.footer.find(".btn-primary").off("click").on("click", () => this.callback(this.folderName.val()));

        this.modal.find(':file').on('change', function () {
            const file = this.files[0];
            if (file.size > 500 * Math.pow(2,20)) {
                showToast("Error", "Please select a file not exceeding 500MB");
            }
            _baseModal.file = file;
        });
    }

    show() {
        this.folderName.val("");
        this.modal.find(':file').val(null);
        super.show();
    }
}
