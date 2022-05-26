function DocumentDetail() {

    this.init = function () {
        this.container = document.getElementById("documentTableBody");
    }

    this.show = function (documentData) {
        this.update(document);
    }

    this.update = function(documentData) {
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
        }

        this.container.innerHTML = "";
        createRow("Owner", documentData.ownerUsername, true);
        createRow("Parent Folder", documentData.parentFolderName, true);
        createRow("Name", documentData.documentName, true);
        createRow("File Type", documentData.documentFileType, true);
        createRow("Creation Date", documentData.documentCreationDate, true);
        createRow("Contents", documentData.documentContents, true);
    }

    this.hide = function() {
        // Clear the div
        this.container.innerHTML = "";
    }
}