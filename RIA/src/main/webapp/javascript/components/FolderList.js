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

        function createSVG(parent) {
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

        function createFolderRow(parent, folder, isSubfolder) {
            parent.setAttribute("class", "folderRow");
            createSVG(parent);
            let iconSeparator = document.createElement("div");
            iconSeparator.setAttribute("class", "folderIconSeparator")
            parent.appendChild(iconSeparator);
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
            } else {
                let nameLabel = document.createElement("label");
                nameLabel.textContent = folder.name;
                parent.appendChild(nameLabel);
            }
            let creationDateLabel = document.createElement("label");
            creationDateLabel.setAttribute("class", "creationDateLabel");
            creationDateLabel.textContent = folder.creationDate;
            parent.appendChild(creationDateLabel);
        }

        if (folders.length > 0) {
            // Show the folder list
            this.container.innerHTML = "";
            folders.forEach((folder) => {
                let row = document.createElement("li");
                createFolderRow(row, folder, false);
                //Add the subfolders
                let subfolderList = document.createElement("ul");
                subfolderList.setAttribute("class", "subfolderList")
                folder.subfolders.forEach((subfolder) => {
                    let subrow = document.createElement("li");
                    createFolderRow(subrow, subfolder, true);
                    subfolderList.appendChild(subrow);
                });
                row.appendChild(subfolderList);
                self.container.appendChild(row);
            })
        } else {
            // Show the empty message
            this.emptyMessagePar.hidden = false;
            this.emptyMessagePar.textContent = "No folders to show. Create a new folder withe the \"Create\" button at the top of the page";
        }
    }

    this.markAsDragSource = function(documentData) {
        let folderID = documentData.parentFolderNumber;
    }

    this.unhide = function() {
        this.container.hidden = false;
    }

    this.hide = function() {
        // Clear the div
        this.container.hidden = true;
        this.emptyMessagePar.hidden = true;
    }
}