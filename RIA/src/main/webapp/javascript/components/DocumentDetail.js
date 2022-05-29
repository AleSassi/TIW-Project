function DocumentDetail() {

    this.init = function () {
        this.container = document.getElementById("documentTableBody");
        this.pageTitle = document.getElementById("pageTitle");
        this.createFolderButton = document.getElementById("createFolderButton");
    }

    this.show = function () {
        //Just like FolderDetail, we do nothing
        this.container.hidden = false;
        this.pageTitle.textContent = "Document Detail";
        this.createFolderButton.hidden = true;
    }

    this.update = function(documentData) {
        var self = this;
        function createRow(title, content, isEven) {
            let row = document.createElement("tr")
            row.setAttribute("class", isEven ? "evenRow" : "oddRow");
            let tableTitle = document.createElement("td");
            tableTitle.setAttribute("class", "rowTitle");
            tableTitle.textContent = title;
            row.appendChild(tableTitle);
            let tableContent = document.createElement("td");
            tableContent.textContent = content;
            row.appendChild(tableContent);
            self.container.appendChild(row);
        }

        this.container.innerHTML = "";
        createRow("Owner", documentData.document.ownerUsername, true);
        createRow("Parent Folder", documentData.folderName, false);
        createRow("Name", documentData.document.name, true);
        createRow("File Type", documentData.document.fileType, false);
        createRow("Creation Date", documentData.document.creationDate, true);
        createRow("Contents", documentData.document.contents, false);
    }

    this.unhide = function() {
        this.createFolderButton.hidden = true;
        this.pageTitle.textContent = "Document Detail";
        this.container.hidden = false;
    }

    this.hide = function() {
        // Clear the div
        this.container.hidden = true;
    }
}