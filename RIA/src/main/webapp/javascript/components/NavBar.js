function NavBar() {

    this.init = function() {
        this.container = document.getElementById("navBar");
        this.homeButton = document.getElementById("homeButton");
        this.createButton = document.getElementById("createButton");
        this.usernameDisplayer = document.getElementById("usernameDisplayer");

        this.homeButton.classList.remove("active");
    }

    this.show = function() {
        //Get the username and display it
        let username = window.sessionStorage.getItem("username");
        this.usernameDisplayer.textContent = "Hi @" + username;
    }

    this.update = function(selectedOptionID) {
        if (selectedOptionID === 0) {
            this.homeButton.classList.add("active")
            this.createButton.classList.remove("active");
        } else if (selectedOptionID === 1) {
            this.homeButton.classList.remove("active")
            this.createButton.classList.add("active");
        } else {
            this.homeButton.classList.remove("active")
            this.createButton.classList.remove("active");
        }
    }
}
