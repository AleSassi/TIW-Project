window.addEventListener('load', function onload() {
    const form = document.getElementById("signupForm");

    form.addEventListener('submit', function onclick(e) {
        const emailField = document.getElementById("email");
        const passwordField = document.getElementById("password");
        const repeatPasswordField = document.getElementById("passwordRepeat");
        const emailErrorLabel = document.getElementById("emailError");
        const repeatPWDLabel = document.getElementById("repeatPasswordError");

        let isValid = true;
        console.log("Checking....");

        if (!(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(emailField.value))) {
            // Show the Error Message for Email
            emailErrorLabel.textContent = "Invalid Email address - JS";
            emailErrorLabel.hidden = false;
            isValid = false;
            e.preventDefault();
        } else {
            emailErrorLabel.hidden = true;
        }
        if (passwordField.value !== repeatPasswordField.value) {
            // Show the Error Message for RepeatPassword
            repeatPWDLabel.textContent = "The Password and RepeatPassword fields do not match - JS";
            repeatPWDLabel.hidden = false;
            isValid = false;
            e.preventDefault();
        } else {
            repeatPWDLabel.hidden = true;
        }
        return isValid;
    })
})