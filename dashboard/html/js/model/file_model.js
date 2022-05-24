/**
 * Data model for file representation model
 */

class FileSystem {
    constructor() {
        this.path = [];
    }

    /**
     * Support move of folder to deeper location
     * If pathName is absolute path then re-assign it to model
     * @param {String} pathName
     */
    moveIn(pathName) {
        let deeperPath = pathName.split('/');
        if (deeperPath[0] === "") {
            deeperPath.shift();
            this.path = deeperPath;
        } else {
            this.path = this.path.concat(deeperPath);
        }
    }

    /**
     * Support move out of folder
     * @param {String} level of folder to jump out
     */
    moveOut(level) {
        for (let i = 0; i < level; i++)
            this.path.pop();
    }

    toString() {
        return '/' + this.path.join('/');
    }
}
