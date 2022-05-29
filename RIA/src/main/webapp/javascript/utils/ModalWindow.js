function ModalWindow() {

    this.show = function(title, message, confirmText, confirmHandler) {
        this.fader = document.getElementById("faderContainer");
        this.fader.setAttribute("class", "fader");

        let modalContent = document.createElement("div");
        modalContent.setAttribute("class", "modal-content");
        let headerText = document.createElement("h2");
        headerText.textContent = title;
        modalContent.appendChild(headerText);
        let modalBodyText = document.createElement("label");
        modalBodyText.textContent = message;
        modalContent.appendChild(modalBodyText);
        let confirmButton = document.createElement("div");
        confirmButton.setAttribute("class", "modalButton");
        confirmButton.textContent = confirmText;
        let cancelButton = document.createElement("div");
        cancelButton.setAttribute("class", "modalButton");
        cancelButton.setAttribute("id", "closeModal");
        cancelButton.textContent = "Cancel";
        modalContent.appendChild(confirmButton);
        modalContent.appendChild(cancelButton)
        this.fader.appendChild(modalContent);
        const self = this;
        closeButton.addEventListener("click", function(e) {
            e.preventDefault();
            self.dismiss();
            confirmHandler(false);
        })
        confirmButton.addEventListener("click", function(e) {
            e.preventDefault();
            self.dismiss();
            confirmHandler(true);
        });
    }

    this.dismiss = function() {
        this.fader.innerHTML = "";
        this.fader.removeAttribute("class")
    }
}