/**
 * A utility class for managing breadcrumbs
 */
export class Breadcrumb {

    /**
     * @param {item} defaultItem default item, even when there is no other item
     * @param {*} items array of items, with .name, .data and (.url or .click)
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

    /**
     * Keep only a specific number of first children in the breadcrumb
     * @param {int} n number of items to keep
     */
    keep(n) {
        this.items = this.items.slice(0, n + 1);
    }

    empty(n) {
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
                bcitem = $(`<li class="breadcrumb-item"><a href="#" onclick="${item.click}">${item.name}</a></li>`)
            }
            else {
                bcitem = $(`<li class="breadcrumb-item"><a href="${item.url}">${item.name}</a></li>`)
            }

            bc.append(bcitem);
        });
    }
}