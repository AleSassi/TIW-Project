window.addEventListener("load", (e) => {
    let controller = new PageController();
    controller.start();
    controller.present(0);

    //Link an event handler to the "New Folder" button
    document.getElementById("createFolderButton").addEventListener("click", function(e) {
        //Add a new, fake cell with the folder name field
        controller.folderList.startCreatingNewFolder();
    });
});