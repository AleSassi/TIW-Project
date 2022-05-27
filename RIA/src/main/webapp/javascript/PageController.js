function PageController() {
    this.navBar = new NavBar();
    this.folderList = new FolderList();
    this.folderDetail = new FolderDetail();
    this.documentDetail = new DocumentDetail();

    this.start = function() {
        //Add the required event handlers for the single page
        this.folderList.init(this);
        this.folderDetail.init(this);
        this.documentDetail.init(this);
        this.navBar.init(this);
        this.navBar.show();
    }

    this.present = function() {
        // This function has to have as arguments: a number, representing the ID of the content to show, and the list of params for that
        let viewID = arguments[0];
        if (viewID === 0) {
            // Present the FolderList
            this.folderDetail.hide();
            this.documentDetail.hide();
            this.folderList.show();
            this.navBar.update(0);
        } else if (viewID === 1) {
            // Present the FolderDetail - argument 1 will have the content of the folder to present
            this.folderList.hide();
            this.documentDetail.hide();
            this.folderDetail.show();
            this.folderDetail.update(arguments[1]);
            this.navBar.update(-1);
        } else if (viewID === 2) {
            // Present the DocumentDetail
            this.navBar.update(-1);
        } else if (viewID === 3) {
            // Present the Create menu
            this.navBar.update(1);
        }
    }

}