function LoginForm() {

    this.init = function() {
        this.usernameField = document.getElementById("username");
        this.usernameErrorLabel = document.getElementById("usernameError");
        this.passwordField = document.getElementById("password");
        this.passwordErrorLabel = document.getElementById("passwordError");
        this.form = document.getElementById("loginForm");
        //Hide error fields at load
        this.usernameErrorLabel.hidden = true;
        this.usernameErrorLabel.textContent = "";
        this.passwordErrorLabel.hidden = true;
        this.passwordErrorLabel.textContent = "";
    }

    this.show = function () {
        //When we show the element nothing will happen (no server requests)
        //We assume that the server also checks for user-logged-in, so in that case the login form will not appear
        //Add the event listeners (still needed to check the input and send the request to the server)
        const self = this;
        this.form.addEventListener("submit", function (e) {
            e.preventDefault();
            let form = e.target.closest("form");
            let isValid = form.checkValidity();
            //Check the username
            if (!self.usernameField.value || self.usernameField.value.size < 1) {
                //Show the Error Message for Email
                self.usernameErrorLabel.textContent = "You must enter your username to sign in";
                self.usernameErrorLabel.hidden = false;
                isValid = false;
            } else {
                self.usernameErrorLabel.hidden = true;
                self.usernameErrorLabel.textContent = "";
            }
            //Check the Password
            if (!self.passwordField.value || self.passwordField.value.size < 8) {
                self.passwordErrorLabel.textContent = "You must enter your password";
                self.passwordErrorLabel.hidden = false;
                isValid = false;
            } else {
                self.passwordErrorLabel.hidden = true;
                self.passwordErrorLabel.textContent = "";
            }

            if (isValid) {
                //Send the request to the server
                post("", form, function(request) {
                    if (request.status === 200) {
                        // Show the folder list
                        sessionStorage.setItem("username", self.usernameField.value);
                        window.location.href = "home";
                    } else if (request.status === 500) {
                        //TODO: Show an error popup
                    } else {
                        //The response contains a JSON file with the error messages
                        let response = JSON.parse(request.responseText);
                        self.update(response)
                    }
                })
            }
        });
    }

    this.update = function(responseObj) {
        //Show the error messages, clearing the Password field
        this.passwordField.textContent = "";
        if (responseObj.usernameError) {
            this.usernameErrorLabel.hidden = false;
            this.usernameErrorLabel.textContent = responseObj.usernameError;
        } else {
            this.usernameErrorLabel.hidden = true;
            this.usernameErrorLabel.textContent = "";
        }
        if (responseObj.passwordError) {
            this.passwordErrorLabel.hidden = false;
            this.passwordErrorLabel.textContent = responseObj.passwordError;
        } else {
            this.passwordErrorLabel.hidden = true;
            this.passwordErrorLabel.textContent = "";
        }
    }

    this.hide = function() {
    }
}