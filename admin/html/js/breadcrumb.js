/**
 * A utility class for managing breadcrumbs
 */
export class Breadcrumb {

    /**
     * @param {*} items array of items, with .name and (.url or .click)
     */
    constructor(defaultItem, items) {
        this.defaultItem = defaultItem;
        if (items) {
            this.items = items;
        }
        else {
            this.items = [ defaultItem ];
        }
    }

    append(item) {
        this.items.push(item);
        this.render();
    }

    clear() {
        if (this.defaultItem) {
            this.items = [ this.defaultItem ];
        }
        else this.items = [];
    }

    render() {
        const bc = $("ol[class=breadcrumb]");
        bc.empty();
        this.items.forEach(item => {
            let bcitem;
            if (item.click) {
                bcitem = $(`<li class="breadcrumb-item"><a href="#" callback="${item.click}">${item.name}</a></li>`)
            }
            else {
                bcitem = $(`<li class="breadcrumb-item"><a href="${item.url}">${item.name}</a></li>`)
            }

            bc.append(bcitem);
        });
    }
}