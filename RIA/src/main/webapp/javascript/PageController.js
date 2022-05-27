function PageController() {
    this.navBar = new NavBar();
    this.folderList = new FolderList();
    this.folderDetail = new FolderDetail();
    this.documentDetail = new DocumentDetail();

    this.start = function() {
        //Add the required event handlers for the single page
        this.folderList.init();
        this.folderDetail.init();
        this.documentDetail.init();
        this.navBar.init();
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
            // Present the FolderDetail
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