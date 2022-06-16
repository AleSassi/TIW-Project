function SignupForm() {

    this.init = function() {
        this.usernameField = document.getElementById("username");
        this.usernameErrorLabel = document.getElementById("usernameError");
        this.emailField = document.getElementById("email");
        this.emailErrorLabel = document.getElementById("emailError");
        this.passwordField = document.getElementById("password");
        this.passwordErrorLabel = document.getElementById("passwordError");
        this.passwordRepeatField = document.getElementById("passwordRepeat");
        this.passwordRepeatErrorLabel = document.getElementById("repeatPasswordError");
        this.form = document.getElementById("signupForm");
        //Hide error fields at load
        this.usernameErrorLabel.hidden = true;
        this.usernameErrorLabel.textContent = "";
        this.passwordErrorLabel.hidden = true;
        this.passwordErrorLabel.textContent = "";
        this.emailErrorLabel.hidden = true;
        this.emailErrorLabel.textContent = "";
        this.passwordRepeatErrorLabel.hidden = true;
        this.passwordRepeatErrorLabel.textContent = "";
    }

    this.show = function () {
        //When we show the element nothing will happen (no server requests)
        //We assume that the server also checks for user-logged-in, so in that case the login form will not appear
        //Add the event listeners (still needed to check the input and send the request to the server)
        const self = this;
        this.form.addEventListener("submit", function (e) {
            e.preventDefault();
            let isValid = self.form.checkValidity();
            //Check the username
            if (!self.usernameField.value || self.usernameField.value.size < 1 || !/[^ ]/.test(self.usernameField.value)) {
                //Show the Error Message for Email
                self.usernameErrorLabel.textContent = "You must enter your username to sign in";
                self.usernameErrorLabel.hidden = false;
                isValid = false;
            } else {
                self.usernameErrorLabel.hidden = true;
                self.usernameErrorLabel.textContent = "";
            }
            //Check the email
            if (!self.emailField.value || self.emailField.value.size < 1 || !/[^ ]/.test(self.emailField.value)) {
                //Show the Error Message for Email
                self.emailErrorLabel.textContent = "You must enter your email to sign up";
                self.emailErrorLabel.hidden = false;
                isValid = false;
            } else if (!(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(self.emailField.value))) {
                // Show the Error Message for Email
                self.emailErrorLabel.textContent = "Invalid Email address";
                self.emailErrorLabel.hidden = false;
                isValid = false;
            } else {
                self.emailErrorLabel.hidden = true;
                self.emailErrorLabel.textContent = "";
            }
            //Check the Password
            if (!self.passwordField.value || self.passwordField.value.size < 8 || !/[^ ]/.test(self.passwordField.value)) {
                self.passwordErrorLabel.textContent = "You must enter your password (at least 8 characters long)";
                self.passwordErrorLabel.hidden = false;
                isValid = false;
            } else {
                self.passwordErrorLabel.hidden = true;
                self.passwordErrorLabel.textContent = "";
            }
            //Check the Repeat Password
            if (!self.passwordRepeatField.value || self.passwordRepeatField.value.size < 1 || !/[^ ]/.test(self.passwordRepeatField.value)) {
                self.passwordRepeatErrorLabel.textContent = "You must repeat your Password to sign up";
                self.passwordRepeatErrorLabel.hidden = false;
                isValid = false;
            } else if (self.passwordField.value !== self.passwordRepeatField.value) {
                self.passwordRepeatErrorLabel.textContent = "The \"Password\" and \"Repeat Password\" fields do not match";
                self.passwordRepeatErrorLabel.hidden = false;
                isValid = false;
            } else {
                self.passwordRepeatErrorLabel.hidden = true;
                self.passwordRepeatErrorLabel.textContent = "";
            }

            if (isValid) {
                //Send the request to the server
                post("signup", self.form, function(request) {
                    if (request.status === 200) {
                        // Show the folder list
                        sessionStorage.setItem("username", self.usernameField.value);
                        window.location.href = "home";
                    } else if (request.status === 500) {
                        //Show an error popup
                        let alert = new Alert();
                        alert.variantID = 0;
                        alert.present("The server encountered an error while processing the request.\n\n" + request.responseText);
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
        this.passwordRepeatField.textContent = "";
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
        if (responseObj.emailError) {
            this.emailErrorLabel.hidden = false;
            this.emailErrorLabel.textContent = responseObj.emailError;
        } else {
            this.emailErrorLabel.hidden = true;
            this.emailErrorLabel.textContent = "";
        }
        if (responseObj.repeatPasswordError) {
            this.passwordRepeatErrorLabel.hidden = false;
            this.passwordRepeatErrorLabel.textContent = responseObj.repeatPasswordError;
        } else {
            this.passwordRepeatErrorLabel.hidden = true;
            this.passwordRepeatErrorLabel.textContent = "";
        }
    }

    this.hide = function() {
    }
}