window.addEventListener('load', function () {
    const form = document.getElementById("loginForm");

    form.addEventListener('submit', function (e) {
        const usernameField = document.getElementById("username");
        const passwordField = document.getElementById("password");
        const usernameErrorLabel = document.getElementById("usernameError");
        const pwdErrorLabel = document.getElementById("passwordError");

        let isValid = true;

        //Check for null values
        if (!usernameField.value || usernameField.value.size < 1) {
            //Show the Error Message for Email
            usernameErrorLabel.textContent = "You must enter your username to sign in";
            usernameErrorLabel.hidden = false;
            isValid = false;
        } else {
            usernameErrorLabel.hidden = true;
        }

        if (!passwordField.value || passwordField.value.size < 8) {
            pwdErrorLabel.textContent = "You must enter your password";
            pwdErrorLabel.hidden = false;
            isValid = false;
        } else {
            pwdErrorLabel.hidden = true;
        }
        if (!isValid) {
            e.preventDefault();
        }
        return isValid;
    })
})