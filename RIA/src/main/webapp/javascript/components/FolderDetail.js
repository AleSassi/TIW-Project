function FolderDetail() {

    this.init = function(pageController) {
        this.emptyMessagePar = document.getElementById("emptyMessage");
        this.pageController = pageController;
    }

    this.show = function (hostingFolderRow) {
        if (this.hostingFolderRow !== hostingFolderRow) {
            if (this.hostingFolderRow) {
                //Remove the previous div
                this.hostingFolderRow.removeChild(this.container);
            }
            //Add the new container
            this.container = document.createElement("ul");
            this.container.setAttribute("class", "subfolderList");
            hostingFolderRow.appendChild(this.container)
            this.hostingFolderRow = hostingFolderRow;
        }
    }

    this.update = function(documents) {
        this.container.innerHTML = "";
        const self = this;

        function createSVG(parent) {
            //Create the SVG icon
            let svgIcon = document.createElement("span");
            svgIcon.setAttribute("class", "folderIcon");
            svgIcon.innerHTML = "<svg xmlns=\"http://www.w3.org/2000/svg\" x=\"0px\" y=\"0px\"\n" +
                "                     width=\"30\" height=\"30\"\n" +
                "                     viewBox=\"0 0 172 172\"\n><g fill=\"none\" fill-rule=\"nonzero\" stroke=\"none\" stroke-width=\"1\" stroke-linecap=\"butt\" stroke-linejoin=\"miter\" stroke-miterlimit=\"10\" stroke-dasharray=\"\" stroke-dashoffset=\"0\" font-family=\"none\" style=\"mix-blend-mode: normal\"><path d=\"M0,172v-172h172v172z\" fill=\"none\"></path><g fill=\"#3498db\"><path d=\"M141.65347,50.4132l-37.26667,-37.26667c-1.07787,-1.07787 -2.53413,-1.67987 -4.05347,-1.67987h-60.2c-6.33533,0 -11.46667,5.13133 -11.46667,11.46667v126.13333c0,6.33533 5.13133,11.46667 11.46667,11.46667h91.73333c6.33533,0 11.46667,-5.13133 11.46667,-11.46667v-94.6c0,-1.51933 -0.602,-2.9756 -1.67987,-4.05347zM103.2,57.33333c-3.1648,0 -5.73333,-2.56853 -5.73333,-5.73333v-29.21707l34.9504,34.9504z\"></path></g></g></svg>"
            parent.appendChild(svgIcon);
        }

        function createDocumentRow(parent, documentData) {
            parent.setAttribute("class", "folderRow");
            parent.setAttribute("draggable", true);
            createSVG(parent);
            let iconSeparator = document.createElement("div");
            iconSeparator.setAttribute("class", "folderIconSeparator")
            parent.appendChild(iconSeparator);
            let nameLabel = document.createElement("a");
            nameLabel.setAttribute("class", "link");
            nameLabel.setAttribute("href", "#");
            nameLabel.textContent = documentData.name;
            parent.appendChild(nameLabel);
            parent.appendChild(iconSeparator);
            nameLabel.addEventListener("click", (e) => {
                //Show the Document Detail
                e.preventDefault();
                get("getDocumentData?document=" + documentData.documentNumber + "&fid=" + documentData.parentFolderNumber, function(request) {
                    if (request.status === 200) {
                        // We have the folder data as JSON - show
                        let documentData = JSON.parse(request.responseText);
                        self.pageController.present(2, documentData);
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
            parent.appendChild(iconSeparator.cloneNode());
            let creationDateLabel = document.createElement("label");
            creationDateLabel.setAttribute("class", "creationDateLabel");
            creationDateLabel.textContent = documentData.creationDate;
            parent.appendChild(creationDateLabel);
            //Drag & Drop
            parent.addEventListener("dragstart", function(e) {
                //The FolderList will handle the drop
                //Cache the document for the drop operation
                self.draggingDocument = documentData;
            })
        }

        if (documents.documents.length > 0) {
            // Show the folder list
            documents.documents.forEach((documentData) => {
                let row = document.createElement("li");
                createDocumentRow(row, documentData);
                self.container.appendChild(row);
            })
        } else {
            // Show the empty message
            this.emptyMessagePar.hidden = false;
            this.emptyMessagePar.textContent = "No documents to show. Create a new document with the \"Create\" button at the top of the page";
        }
    }

    this.terminateDragSession = function() {
        this.draggingDocument = null;
    }

    this.unhide = function() {
        this.container.hidden = false;
    }

    this.hide = function() {
        // Clear the div
        if (this.container) {
            this.container.hidden = true;
        }
        this.emptyMessagePar.hidden = true;
    }
}