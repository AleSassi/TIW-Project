function FolderDetail() {

    this.init = function() {
        this.container = document.getElementById("documentList");
        this.emptyMessagePar = document.getElementById("emptyMessage");
    }

    this.show = function (folderID) {
        const self = this;
        // Get the username from the Browser session
        let username = sessionStorage.getItem("username");
        //Ask the server which folders are owned by the user and display
        get("GetFolderContentData", new function(request) {
            if (request.status === 200) {
                // We got the data from the server. Now parse it and show the list of folders to screen
                let documents = JSON.parse(request.responseText);
                // Show the folder list
                self.update(documents);
            } else if (request.status === 403) {
                //TODO: Log the user out and show the login page
            } else {
                //TODO: Show an error popup
            }
        });
    }

    this.update = function(documents) {
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
            createSVG(parent);
            let iconSeparator = document.createElement("div");
            iconSeparator.setAttribute("class", "folderIconSeparator")
            parent.appendChild(iconSeparator);
            let nameLabel = document.createElement("label");
            nameLabel.textContent = documentData.name;
            parent.appendChild(nameLabel);
            parent.appendChild(iconSeparator);
            let openLink = document.createElement("a");
            openLink.setAttribute("class", "link");
            openLink.setAttribute("href", "#");
            openLink.textContent = "Open";
            openLink.addEventListener("click", (e) => {
                //TODO: Show the Document Detail

            })
            parent.appendChild(openLink);
            parent.appendChild(iconSeparator.cloneNode());
            let moveLink = document.createElement("a");
            moveLink.setAttribute("class", "link");
            moveLink.setAttribute("href", "#");
            moveLink.textContent = "Move";
            moveLink.addEventListener("click", (e) => {
                //TODO: Start Moving the Document

            })
            parent.appendChild(moveLink);
            parent.appendChild(iconSeparator.cloneNode());
            let creationDateLabel = document.createElement("label");
            creationDateLabel.setAttribute("class", "creationDateLabel");
            creationDateLabel.textContent = folder.creationDate;
            parent.appendChild(creationDateLabel);
        }

        if (documents.length > 0) {
            // Show the folder list
            this.container.innerHTML = "";
            const self = this;
            documents.forEach((documentData) => {
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

    this.hide = function() {
        // Clear the div
        this.container.innerHTML = "";
        this.emptyMessagePar.hidden = true;
    }
}