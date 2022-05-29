function PageController() {
    this.navBar = new NavBar();
    this.folderList = new FolderList();
    this.folderDetail = new FolderDetail();
    this.documentDetail = new DocumentDetail();
    this.documentCreate = new CreateDocumentForm();
    this.trash = new Trash();

    this.start = function() {
        //Add the required event handlers for the single page
        this.folderList.init(this);
        this.folderDetail.init(this);
        this.documentDetail.init(this);
        this.documentCreate.init(this);
        this.navBar.init(this);
        this.navBar.show();
        //Get the back button and add the back event
        const self = this;
        this.previousViewIDs = [];
        this.backButton = document.getElementById("backButton");
        this.backButton.addEventListener("click", (e) => {
            e.preventDefault();
            self.presentFromBack(this.previousViewIDs.pop());
        })
    }

    this.present = function() {
        // This function has to have as arguments: a number, representing the ID of the content to show, and the list of params for that
        let viewID = arguments[0];
        this.previousViewIDs.push(this.currentViewID);
        this.backButton.hidden = false;
        if (viewID === 0) {
            // Present the FolderList
            this.folderDetail.hide();
            this.documentDetail.hide();
            this.folderList.show();
            this.navBar.update(0);
            this.currentViewID = 0;
            this.backButton.hidden = true;
        } else if (viewID === 1) {
            // Present the FolderDetail - argument 1 will have the content of the folder to present
            this.documentDetail.hide();
            //this.folderList.show();
            this.folderDetail.show(arguments[2]);
            this.folderDetail.update(arguments[1]);
            this.navBar.update(0);
            this.currentViewID = 1;
            this.backButton.hidden = true;
        } else if (viewID === 2) {
            // Present the DocumentDetail - argument 1 will have the content of the document to present
            this.folderList.hide();
            this.folderDetail.hide();
            this.documentDetail.show();
            this.documentDetail.update(arguments[1]);
            this.navBar.update(-1);
            this.currentViewID = 2;
        } else if (viewID === 3) {
            // Present the Create Document menu
            this.documentCreate.show(arguments[1], arguments[2]);
            this.currentViewID = 3;
        }

    }

    this.presentFromBack = function() {
        // This function has to have as arguments: a number, representing the ID of the content to show, and the list of params for that
        let viewID = arguments[0];
        this.backButton.hidden = false;
        if (viewID === 0) {
            // Present the FolderList
            this.folderDetail.hide();
            this.documentDetail.hide();
            this.folderList.unhide();
            this.navBar.update(0);
            this.currentViewID = 0;
            this.backButton.hidden = true;
        } else if (viewID === 1) {
            // Present the FolderList with detail
            this.documentDetail.hide();
            this.folderList.unhide();
            this.folderDetail.unhide();
            this.navBar.update(0);
            this.currentViewID = 1;
            this.backButton.hidden = true;
        } else if (viewID === 2) {
            // Present the DocumentDetail - argument 1 will have the content of the document to present
            this.folderList.hide();
            this.folderDetail.hide();
            this.documentDetail.unhide();
            this.navBar.update(-1);
            this.currentViewID = 2;
        } else if (viewID === 3) {
            // Present the Create menu
            this.documentCreate.hide();
            this.currentViewID = 3;
        }

    }

    this.draggingItem = function() {
        return this.folderDetail.draggingDocument ?? this.folderList.draggingFolder;
    }

    this.draggingCell = function() {
        return this.folderDetail.draggingCell ?? this.folderList.draggingCell;
    }

    this.validateDropLocation = function(dropLocationID) {
        let isDocument = true;
        if (this.draggingItem().folderNumber) {
            isDocument = false;
        }
        return isDocument && this.folderDetail.draggingDocument.parentFolderNumber !== dropLocationID;
    }

    this.terminateDragSession = function() {
        this.folderDetail.terminateDragSession();
        this.folderList.terminateDragSession();
    }
}