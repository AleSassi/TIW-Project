function FolderList() {

    this.init = function(pageController) {
        this.container = document.getElementById("folderList");
        this.emptyMessagePar = document.getElementById("emptyMessage");
        this.pageController = pageController;
    }

    this.show = function () {
        this.container.hidden = false;
        const self = this;
        // Get the username from the Browser session
        let username = sessionStorage.getItem("username");
        //Ask the server which folders are owned by the user and display
        get("GetFolderListData", function(request) {
            if (request.status === 200) {
                // We got the data from the server. Now parse it and show the list of folders to screen
                let folders = JSON.parse(request.responseText);
                // Show the folder list
                self.update(folders);
            } else if (request.status === 403) {
                //Logout
                window.sessionStorage.removeItem("username");
                window.location.href = "";
            } else {
                //Show an error popup
                let alert = new Alert();
                alert.variantID = 0;
                alert.present("The server encountered an error while processing the request.\n\n" + request.responseText);
            }
        });
    }

    this.update = function(folders) {
        this.container.innerHTML = "";
        const self = this;
        if (folders.length > 0) {
            // Show the folder list
            this.container.innerHTML = "";
            this.emptyMessagePar.hidden = true;
            this.createTrashCell();
            folders.forEach((folder) => {
                let row = document.createElement("li");
                self.createFolderRow(row, folder, false);
                self.container.appendChild(row);
            })
        } else {
            // Show the empty message
            this.emptyMessagePar.hidden = false;
            this.emptyMessagePar.textContent = "No folders to show. Create a new folder with the \"+\" button at the top of the page";
        }
    }

    this.startCreatingNewFolder = function(parentFolderRow, parentFolderID) {
        let previouslyAddedFolderCreator = document.getElementById("createFolderRow");
        if (previouslyAddedFolderCreator) {
            previouslyAddedFolderCreator.remove();
        }
        const self = this;
        //Create a fake row at the bottom, with an input field
        let inputRow = document.createElement("li");
        inputRow.setAttribute("class", "folderRow");
        inputRow.setAttribute("id", "createFolderRow");
        this.createSVG(inputRow);
        let iconSeparator = document.createElement("div");
        iconSeparator.setAttribute("class", "folderIconSeparator")
        inputRow.appendChild(iconSeparator);
        let nameLabel = document.createElement("label");
        nameLabel.textContent = "Folder Title:";
        inputRow.appendChild(nameLabel);
        inputRow.appendChild(iconSeparator.cloneNode());
        let form = document.createElement("form");
        let field = document.createElement("input");
        field.setAttribute("type", "text");
        field.setAttribute("name", "createForm_folderTitle");
        field.setAttribute("id", "createForm_folderTitle");
        field.setAttribute("class", "fullHeightElement textField");
        field.setAttribute("placeholder", "Folder Title");
        let errorLabel = document.createElement("label");
        errorLabel.setAttribute("for", "createForm_folderTitle");
        errorLabel.setAttribute("class", "errorLabel");
        errorLabel.setAttribute("id", "createForm_folderTitleError");
        if (parentFolderID) {
            let hiddenField = document.createElement("input");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", "parentFolderID");
            hiddenField.setAttribute("value", parentFolderID);
            form.appendChild(hiddenField);
        }
        let submitButton = document.createElement("input");
        submitButton.setAttribute("type", "submit");
        submitButton.setAttribute("class", "filledButton contentTypeFormButton");
        submitButton.setAttribute("value", "Create");
        form.appendChild(field);
        form.appendChild(errorLabel);
        form.appendChild(submitButton);
        inputRow.appendChild(form);
        let cancelButton = document.createElement("a");
        cancelButton.href = "";
        cancelButton.textContent = "Cancel";
        cancelButton.setAttribute("class", "link cancelLink");
        inputRow.appendChild(cancelButton);
        if (parentFolderRow) {
            parentFolderRow.appendChild(inputRow);
        } else {
            this.container.appendChild(inputRow);
        }

        submitButton.addEventListener("click", function(e) {
            e.preventDefault();
            if (field.value) {
                post("CreateFolder", form, function(request) {
                    if (request.status === 200) {
                        //Create the folder row
                        let folder = JSON.parse(request.responseText);
                        self.cancelAddFolder();
                        let newRow = document.createElement("li");
                        newRow.setAttribute("class", "folderRow");
                        if (parentFolderRow) {
                            self.createFolderRow(newRow, folder, true);
                            parentFolderRow.appendChild(newRow);
                        } else {
                            self.createFolderRow(newRow, folder, false);
                            self.container.appendChild(newRow);
                        }
                    } else if (request.status === 403) {
                        //Logout
                        window.sessionStorage.removeItem("username");
                        window.location.href = "";
                    } else {
                        errorLabel.textContent = request.responseText;
                    }
                });
            } else {
                errorLabel.textContent = "You must enter the name of the new folder";
            }
        });
        cancelButton.addEventListener("click", function(e) {
            e.preventDefault();
            self.cancelAddFolder();
        });
    }

    this.cancelAddFolder = function() {
        let createFolderRow = document.getElementById("createFolderRow");
        createFolderRow.remove();
    }

    this.unhide = function() {
        this.container.hidden = false;
    }

    this.hide = function() {
        // Clear the div
        this.container.hidden = true;
        this.emptyMessagePar.hidden = true;
    }

    this.createSVG = function(parent) {
        //Create the SVG icon
        let svgIcon = document.createElement("svg");
        svgIcon.setAttribute("xmlns", "http://www.w3.org/2000/svg");
        svgIcon.setAttribute("x", "0px");
        svgIcon.setAttribute("y", "0px");
        svgIcon.setAttribute("width", "24px");
        svgIcon.setAttribute("height", "24px");
        svgIcon.setAttribute("viewBox", "0 0 172 172");
        svgIcon.setAttribute("class", "folderIcon");
        svgIcon.innerHTML = "<g fill=\"none\" fill-rule=\"nonzero\" stroke=\"none\" stroke-width=\"1\" stroke-linecap=\"butt\" stroke-linejoin=\"miter\" stroke-miterlimit=\"10\" stroke-dasharray=\"\" stroke-dashoffset=\"0\" font-family=\"none\" font-weight=\"none\" font-size=\"none\" text-anchor=\"none\" style=\"mix-blend-mode: normal\">\n" +
            "                    <path d=\"M0,172v-172h172v172z\" fill=\"none\">\n" +
            "                    </path>\n" +
            "                    <g fill=\"#007aff\">\n" +
            "                        <path d=\"M22.93333,17.2c-6.33533,0 -11.46667,5.13133 -11.46667,11.46667v17.2h63.06667h86v-5.73333c0,-6.33533 -5.13133,-11.46667 -11.46667,-11.46667h-84.85781l-3.53854,-5.9013c-2.06973,-3.45147 -5.80124,-5.56536 -9.83177,-5.56536zM17.2,57.33333c-3.1648,0 -5.73333,2.56853 -5.73333,5.73333v68.8c0,6.33533 5.13133,11.46667 11.46667,11.46667h126.13333c6.33533,0 11.46667,-5.13133 11.46667,-11.46667v-68.8c0,-3.1648 -2.56853,-5.73333 -5.73333,-5.73333z\">\n" +
            "                        </path>\n" +
            "                    </g>\n" +
            "                </g>"
        parent.appendChild(svgIcon);
    }

    this.createFolderRow = function(parent, folder, isSubfolder) {
        const self = this;
        parent.setAttribute("class", "folderRow");
        this.createSVG(parent);
        let iconSeparator = document.createElement("div");
        iconSeparator.setAttribute("class", "folderIconSeparator")
        parent.appendChild(iconSeparator);
        let newSubfolderButton;
        if (isSubfolder) {
            let nameLink = document.createElement("a");
            nameLink.setAttribute("class", "link");
            nameLink.setAttribute("href", "#");
            nameLink.textContent = folder.name;
            nameLink.addEventListener("click", (e) => {
                //Show the Folder Detail
                e.preventDefault();
                get("getFolderDetailData?fid=" + folder.folderNumber, function(request) {
                    if (request.status === 200) {
                        // We have the folder data as JSON - show
                        let folderDetailData = JSON.parse(request.responseText);
                        self.pageController.present(1, folderDetailData, parent);
                    } else if (request.status === 403) {
                        //Logout
                        window.sessionStorage.removeItem("username");
                        window.location.href = "";
                    } else {
                        //Show an error alert
                        let alert = new Alert();
                        alert.variantID = 0;
                        alert.present("The server encountered an error while processing the request.\n\n" + request.responseText);
                    }
                })
            })
            parent.appendChild(nameLink);
            parent.appendChild(iconSeparator.cloneNode());
            let newDocumentButton = document.createElement("a");
            newDocumentButton.setAttribute("id", "addDocumentButton_" + folder.folderNumber);
            newDocumentButton.href = "#";
            newDocumentButton.setAttribute("class", "link");
            newDocumentButton.textContent = "Add Document";
            parent.appendChild(newDocumentButton);
            //Add the event handler
            newDocumentButton.addEventListener("click", function(e) {
                e.preventDefault();
                self.pageController.present(3, folder.folderNumber, nameLink);
            })
        } else {
            let nameLabel = document.createElement("label");
            nameLabel.textContent = folder.name;
            parent.appendChild(nameLabel);
            parent.appendChild(iconSeparator.cloneNode());
            newSubfolderButton = document.createElement("a");
            newSubfolderButton.setAttribute("id", "addSubfolderButton_" + folder.folderNumber);
            newSubfolderButton.href = "#";
            newSubfolderButton.setAttribute("class", "link");
            newSubfolderButton.textContent = "Add Subfolder";
            parent.appendChild(newSubfolderButton);
        }
        let creationDateLabel = document.createElement("label");
        creationDateLabel.setAttribute("class", "creationDateLabel");
        creationDateLabel.textContent = folder.creationDate;
        parent.appendChild(creationDateLabel);
        //Drag start events to drag to the trash can
        parent.draggable = true;
        parent.addEventListener("dragstart", function(e) {
            e.stopPropagation();
            self.draggingFolder = folder;
            self.draggingCell = parent;
        });
        if (isSubfolder) {
            //Drag & Drop
            parent.addEventListener("dragover", function(e) {
                e.preventDefault()
                if (self.pageController.validateDropLocation(folder.folderNumber)) {
                    parent.classList.remove("dropをできない");
                    parent.classList.add("dropをしよう");
                } else {
                    parent.classList.add("dropをできない");
                    parent.classList.remove("dropをしよう");
                }
            })
            parent.addEventListener("dragleave", function(e) {
                e.preventDefault()
                parent.classList.remove("dropをできない");
                parent.classList.remove("dropをしよう");
            })
            parent.addEventListener("drop", function(e) {
                parent.classList.remove("dropをできない");
                parent.classList.remove("dropをしよう");
                if (self.pageController.validateDropLocation(folder.folderNumber)) {
                    post("MoveDocument?docid=" + self.pageController.draggingItem().documentNumber + "&fid=" + folder.folderNumber, null, function(request) {
                        if (request.status === 200) {
                            //Autoclick on this element
                            let clickEvent = new Event("click");
                            parent.querySelector("a").dispatchEvent(clickEvent);
                        } else if (request.status === 403) {
                            //Logout
                            window.sessionStorage.removeItem("username");
                            window.location.href = "";
                        } else {
                            //Show an error alert
                            let alert = new Alert();
                            alert.variantID = 0;
                            alert.present("The server encountered an error while processing the request.\n\n" + request.responseText);
                        }
                    })
                }
            })
        } else {
            //Add the subfolders
            let subfolderList = document.createElement("ul");
            subfolderList.setAttribute("class", "subfolderList")
            folder.subfolders.forEach((subfolder) => {
                let subrow = document.createElement("li");
                self.createFolderRow(subrow, subfolder, true);
                subfolderList.appendChild(subrow);
            });
            parent.appendChild(subfolderList);
            //Add the New Subfolder button handler
            newSubfolderButton.addEventListener("click", function(e) {
                e.preventDefault();
                self.startCreatingNewFolder(subfolderList, folder.folderNumber);
            })
        }
    }

    this.createTrashCell = function() {
        let trashCell = document.createElement("li");
        trashCell.setAttribute("class", "folderRow");
        trashCell.setAttribute("id", "trashCell");
        this.createSVG(trashCell);
        let iconSeparator = document.createElement("div");
        iconSeparator.setAttribute("class", "folderIconSeparator");
        let label = document.createElement("label");
        label.textContent = "Trash";
        trashCell.appendChild(iconSeparator);
        trashCell.appendChild(label);
        this.container.appendChild(trashCell);
        var self = this;
        this.pageController.trash.init(this.pageController, function() {
            self.pageController.draggingCell().remove();
        });
    }

    this.terminateDragSession = function() {
        this.draggingFolder = null;
        this.draggingCell = null;
    }
}