window.addEventListener('load', function onload() {
    const form = document.getElementById("signupForm");

    form.addEventListener('submit', function onclick(e) {
        const emailField = document.getElementById("email");
        const passwordField = document.getElementById("password");
        const repeatPasswordField = document.getElementById("passwordRepeat");
        let isValid = true;
        console.log("Checking....");

        if (!(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(emailField.value))) {
            // Show the Error Message for Email
            const emailErrorLabel = document.getElementById("emailError");
            emailErrorLabel.textContent = "Invalid Email address - JS";
            emailErrorLabel.hidden = false;
            isValid = false;
            e.preventDefault();
        }
        if (passwordField.value !== repeatPasswordField.value) {
            // Show the Error Message for RepeatPassword
            const repeatPWDLabel = document.querySelectorAll('[for="repeatPassword"][class="errorLabel"]');
            repeatPWDLabel.value = "The Password and RepeatPassword fields do not match - JS";
            isValid = false;
            e.preventDefault();
        }
        return isValid;
    })
})