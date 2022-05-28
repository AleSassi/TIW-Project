function CreateItemForm(variant) {
    this.variant = variant; //0 = folder, 1 = document

    this.show = function(parentFolderID) {
        function createField(title, placeholder, required, fieldID) {
            let fieldContainer = document.createElement("div");
            let titleLabel = document.createElement("label")
            titleLabel.setAttribute("for", fieldID);
            let inputField = document.createElement("input");
            inputField.setAttribute("type", "text");
            inputField.setAttribute("name", fieldID);
            inputField.setAttribute("id", fieldID);
            inputField.setAttribute("class", "fullHeightElement textField");
            inputField.setAttribute("placeholder", placeholder);
            if (required) {
                inputField.required = true;
            }
            let errorLabel = document.createElement("label");
            errorLabel.setAttribute("for", fieldID);
            errorLabel.setAttribute("class", "errorLabel");
            errorLabel.setAttribute("id", fieldID + "Error");
            errorLabel.textContent = "Some error here";
            fieldContainer.appendChild(titleLabel);
            fieldContainer.appendChild(inputField);
            fieldContainer.appendChild(errorLabel);
            return fieldContainer;
        }

        //Show a Fader view (the container)
        let fader = document.createElement("div");
        fader.setAttribute("class", "fader");
        //In the Fader view, add the elements that make the form
        let form = document.createElement("form");
        let formContainer = document.createElement("div");
        formContainer.className = "loginFormContainer";
        let formFieldContainer = document.createElement("div");
        formFieldContainer.className = "loginForm";
        let titleLabel = document.createElement("label");
        titleLabel.className = "title";
        titleLabel.textContent = "Create a new " + (this.variant === 0 ? "Folder" : "Document");

    }
}