function DocumentDetail() {

    this.init = function () {
        this.container = document.getElementById("documentTableBody");
    }

    this.show = function () {
        //Just like FolderDetail, we do nothing
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

    this.hide = function() {
        // Clear the div
        this.container.innerHTML = "";
    }
}