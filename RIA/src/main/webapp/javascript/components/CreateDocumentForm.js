function CreateDocumentForm() {

    this.init = function(pageController) {
        this.pageController = pageController;
    }

    this.show = function(parentFolderID, parentFolderNameLink) {
        const self = this;
        function createField(title, placeholder, required, fieldID, isResizable) {
            let fieldContainer = document.createElement("div");
            let titleLabel = document.createElement("label")
            titleLabel.setAttribute("for", fieldID);
            titleLabel.setAttribute("id", fieldID + "Label");
            let inputField = document.createElement(isResizable ? "textArea" : "input");
            inputField.setAttribute("type", "text");
            inputField.setAttribute("name", fieldID);
            inputField.setAttribute("id", fieldID);
            inputField.setAttribute("class", "fullHeightElement textField");
            if (isResizable) {
                inputField.setAttribute("class", "textField resizableInput fullHeightElement");
            }
            inputField.setAttribute("placeholder", placeholder);
            if (required) {
                inputField.required = true;
            }
            let errorLabel = document.createElement("label");
            errorLabel.setAttribute("for", fieldID);
            errorLabel.setAttribute("class", "errorLabel");
            errorLabel.setAttribute("id", fieldID + "Error");
            fieldContainer.appendChild(titleLabel);
            fieldContainer.appendChild(inputField);
            fieldContainer.appendChild(errorLabel);
            return fieldContainer;
        }

        //Show a Fader view (the container)
        this.fader = document.getElementById("faderContainer");
        this.fader.setAttribute("class", "fader");
        //In the Fader view, add the elements that make the form
        let form = document.createElement("form");
        let formContainer = document.createElement("div");
        formContainer.className = "loginFormContainer";
        let formFieldContainer = document.createElement("div");
        formFieldContainer.className = "loginForm";
        let titleLabel = document.createElement("label");
        titleLabel.className = "title";
        titleLabel.textContent = "Create a new Document";
        formFieldContainer.appendChild(titleLabel);
        let docNameField = createField("Document Name:", "Document Name", true, "documentName");
        formFieldContainer.appendChild(docNameField);
        let docExtensionField = createField("Document Extension:", "Document Extension", true, "documentFileType");
        formFieldContainer.appendChild(docExtensionField);
        let docContentField = createField("Document Content:", "Document Content", true, "documentContent", true);
        formFieldContainer.appendChild(docContentField);
        let parentHiddenInput = document.createElement("input");
        parentHiddenInput.setAttribute("type", "hidden");
        parentHiddenInput.setAttribute("name", "parentFolder");
        parentHiddenInput.setAttribute("value", parentFolderID);
        formFieldContainer.appendChild(parentHiddenInput);
        let submit = document.createElement("input");
        submit.setAttribute("type", "submit");
        submit.setAttribute("class", "filledButton contentTypeFormButton");
        submit.setAttribute("value", "Create Document")
        formFieldContainer.appendChild(submit);
        let cancel = document.createElement("a");
        cancel.href = "#";
        cancel.setAttribute("class", "link");
        cancel.textContent = "Cancel";
        formFieldContainer.appendChild(cancel);
        formContainer.appendChild(formFieldContainer);
        form.appendChild(formContainer);
        this.fader.appendChild(form);
        //Event handlers
        cancel.addEventListener("click", (e) => {
            e.preventDefault();
            self.hide();
        });
        submit.addEventListener("click", function(e) {
            e.preventDefault();
            //Check the input fields, display an error if necessary
            let nameField = document.getElementById("documentName");
            let nameError = document.getElementById("documentNameError");
            let fileExtField = document.getElementById("documentFileType");
            let fileExtError = document.getElementById("documentFileTypeError");
            let contentField = document.getElementById("documentContent");
            let contentError = document.getElementById("documentContentError");
            let isValid = form.checkValidity();
            if (isValid) {
                if (nameField.value && /[^ ]/.test(nameField.value)) {
                    nameError.textContent = "";
                } else {
                    nameError.textContent = "You must enter a valid document name";
                    isValid = false;
                }
                if (fileExtField.value && /^[.][^\s]+/.test(fileExtField.value)) {
                    fileExtError.textContent = "";
                } else {
                    fileExtError.textContent = "You must enter a valid file extension";
                    isValid = false;
                }
                if (contentField.value && /[^ ]/.test(nameField.value)) {
                    contentError.textContent = "";
                } else {
                    contentError.textContent = "You must enter some content";
                    isValid = false;
                }
                if (parentHiddenInput.value !== ("" + parentFolderID)) {
                    isValid = false;
                }
                if (isValid) {
                    post("CreateDocument", form, function(request) {
                        if (request.status === 200) {
                            //Autoclick on the fader to dismiss
                            cancel.dispatchEvent(new Event("click"));
                            //Autoclick on the parent subfolder to update the list
                            parentFolderNameLink.dispatchEvent(new Event("click"));
                        } else if (request.status === 403) {
                            //Logout
                            window.sessionStorage.removeItem("username");
                            window.location.href = "";
                        } else {
                            //Load the JSON with errors
                            let response = JSON.parse(request.responseText);
                            nameError.textContent = response.documentNameError;
                            fileExtError.textContent = response.documentExtensionError;
                            contentError.textContent = response.documentContentError;
                        }
                    })
                }
            }
        })
    }

    this.hide = function() {
        this.fader.innerHTML = "";
        this.fader.removeAttribute("class")
        this.fader = this.fader.cloneNode(); //To remove event handlers
    }
}