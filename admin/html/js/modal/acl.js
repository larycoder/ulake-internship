import { BaseModal } from "./base.js";
import { fileApi, aclApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

/**
 * A modal for showing image retrieval results
 */
export class AclModal extends BaseModal {
    constructor(callback) {
        super(callback, "#acl-modal");
    }

    /**
     * Show a bunch of images
     * @param {array} result of image retrieval
     */
    async show(results) {
        const files = await this.fetchImageInfo(results);
        this.render(results, files);
        this.modal.modal("show");
    }

    /**
     * Fetch images from core
     * @param {array} images retrieved
     */
    async fetchImageInfo(results) {
        // find unique ids then fetch info at once
        const ids = [... new Set(results.map(f => f.fid))];
        return await fileApi.many(ids);
    }

    /**
     * Render images onto the modal gallery
     * @param {array}} results of image retrieval
     * @param {array} files returned file info from folder.
     */
    async render(results, files) {

        var media = {
            images: [],
            links: [],
            texts: []
        }
        console.log("render gallery");
        console.log("results", results);
        console.log("files", files);
        for (const result of results) {
            const file = files.find(f => f.id === result.fid);
            if (file) {
                const image = await dashboardObjectApi.download(file.id);
                media.images.push(image);
                media.links.push("#");
                media.texts.push(file.name);
            }
        }
        const gallery = $("<div id='flex-gallery-container'></div>");
        this.body.empty().append(gallery);
        gallery.addFlexImages(media).flexGallery();
    }
}
