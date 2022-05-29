function Trash() {

    this.init = function(pageController, completionHandler) {
        this.pageController = pageController;
        this.trashCell = document.getElementById("trashCell");
        //Drag & Drop
        const self = this;
        this.trashCell.addEventListener("dragover", function(e) {
            e.preventDefault()
            self.trashCell.classList.add("dropをしよう");
        })
        this.trashCell.addEventListener("dragleave", function(e) {
            e.preventDefault()
            self.trashCell.classList.remove("dropをしよう");
        })
        this.trashCell.addEventListener("drop", function(e) {
            self.trashCell.classList.remove("dropをしよう");
            self.showModalPopup(completionHandler);
        })
    }

    this.showModalPopup = function(completionHandler) {
        const self = this;
        let modalWindow = new ModalWindow();
        modalWindow.show("Do you want to delete the item?", "If you delete this item, it will not be possible for you to recover it and you will never be able to access its content again.", "Delete Item", function(confirmed) {
            if (confirmed) {
                //Post the action to the server
                let isDocument = false;
                if (self.pageController.draggingItem().documentNumber) {
                    isDocument = true;
                }
                let queryString = "Delete?fid=" + self.pageController.draggingItem().folderNumber;
                if (isDocument) {
                    queryString = "Delete?docid=" + self.pageController.draggingItem().documentNumber;
                }
                post(queryString, null, function(request) {
                    if (request.status === 200) {
                        //Update the list
                        completionHandler();
                        self.pageController.terminateDragSession();
                    } else if (request.status === 403) {
                        //Logout
                        window.sessionStorage.removeItem("username");
                        window.location.href = "";
                    } else {
                        //Show an error alert
                        let alert = new Alert();
                        alert.variantID = 0;
                        alert.present("The server encountered an error while processing the request.\n\n" + request.responseText);
                        self.pageController.terminateDragSession();
                    }
                })
            } else {
                //End the drag session
                self.pageController.terminateDragSession();
            }
        });
    }
}