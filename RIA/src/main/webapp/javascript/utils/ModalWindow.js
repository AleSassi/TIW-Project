function ModalWindow() {

    this.show = function(title, message, confirmText, confirmHandler) {
        this.fader = document.getElementById("faderContainer");
        this.fader.setAttribute("class", "fader");

        let modalContent = document.createElement("div");
        modalContent.setAttribute("class", "modal-content");
        let modalHeader = document.createElement("div");
        modalHeader.setAttribute("class", "modal-header");
        let closeButton = document.createElement("span");
        closeButton.setAttribute("class", "closeModal");
        closeButton.setAttribute("id", "closeModal");
        closeButton.textContent = "&times;";
        let headerText = document.createElement("h2");
        headerText.textContent = title;
        modalHeader.appendChild(closeButton);
        modalHeader.appendChild(headerText);
        modalContent.appendChild(modalHeader);
        let modalBody = document.createElement("div");
        modalBody.setAttribute("class", "modal-body");
        let modalBodyText = document.createElement("label");
        modalBodyText.textContent = message;
        modalBody.appendChild(modalBodyText);
        modalContent.appendChild(modalBody);
        let modalFooter = document.createElement("div");
        modalFooter.setAttribute("class", "modal-footer");
        let confirmButton = document.createElement("span");
        confirmButton.textContent = confirmText;
        modalFooter.appendChild(confirmButton);
        modalContent.appendChild(modalFooter);
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