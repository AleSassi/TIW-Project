window.addEventListener('load', function () {
    const form = document.getElementById("signupForm");

    form.addEventListener('submit', function (e) {
        const emailField = document.getElementById("email");
        const passwordField = document.getElementById("password");
        const repeatPasswordField = document.getElementById("passwordRepeat");
        const emailErrorLabel = document.getElementById("emailError");
        const pwdErrorLabel = document.getElementById("passwordError");
        const repeatPWDLabel = document.getElementById("repeatPasswordError");

        let isValid = true;

        //Check for null values
        if (!emailField.value || emailField.value.size < 1) {
            //Show the Error Message for Email
            emailErrorLabel.textContent = "You must enter your email to sign up";
            emailErrorLabel.hidden = false;
            isValid = false;
            e.preventDefault();
        } else if (!(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(emailField.value))) {
            // Show the Error Message for Email
            emailErrorLabel.textContent = "Invalid Email address";
            emailErrorLabel.hidden = false;
            isValid = false;
            e.preventDefault();
        } else {
            emailErrorLabel.hidden = true;
        }

        if (!passwordField.value || passwordField.value.size < 8) {
            pwdErrorLabel.textContent = "You must enter a Password at least 8 characters long";
            pwdErrorLabel.hidden = false;
            isValid = false;
            e.preventDefault();
        } else if (!repeatPasswordField.value || repeatPasswordField.value.size < 1) {
            pwdErrorLabel.hidden = true
            repeatPWDLabel.textContent = "You must repeat your password to sign up";
            repeatPWDLabel.hidden = false;
            isValid = false;
            e.preventDefault();
        } else if (passwordField.value !== repeatPasswordField.value) {
            // Show the Error Message for RepeatPassword
            pwdErrorLabel.hidden = true
            repeatPWDLabel.textContent = "The \"Password\" and \"Repeat Password\" fields do not match";
            repeatPWDLabel.hidden = false;
            isValid = false;
            e.preventDefault();
        } else {
            repeatPWDLabel.hidden = true;
            pwdErrorLabel.hidden = true;
        }
        return isValid;
    })
})