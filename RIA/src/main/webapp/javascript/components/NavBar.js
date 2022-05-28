function NavBar() {

    this.init = function(pageController) {
        this.container = document.getElementById("navBar");
        this.homeButton = document.getElementById("homeButton");
        this.usernameDisplayer = document.getElementById("usernameDisplayer");
        this.logoutButton = document.getElementById("logoutButton");
        this.pageController = pageController;

        this.homeButton.classList.remove("active");
    }

    this.show = function() {
        //Get the username and display it
        let username = window.sessionStorage.getItem("username");
        this.usernameDisplayer.textContent = "Hi @" + username;

        const self = this;
        this.homeButton.addEventListener("click", (e) => {
            e.preventDefault();
            //Show the Home page
            self.pageController.present(0);
        });
        this.logoutButton.addEventListener("click", (e) => {
            e.preventDefault();
            //Perform the logout action
            get("logout", function(request) {
                if (request.status === 200) {
                    //Logout
                    window.sessionStorage.removeItem("username");
                    window.location.href = "/";
                } else {
                    //Server error - show alert
                    let alert = new Alert();
                    alert.variantID = 0;
                    alert.present("The server encountered an error while processing the request.\n\n" + request.responseText);
                }
            })
        });
    }

    this.update = function(selectedOptionID) {
        if (selectedOptionID === 0) {
            this.homeButton.classList.add("active")
        } else if (selectedOptionID === 1) {
            this.homeButton.classList.remove("active")
        } else {
            this.homeButton.classList.remove("active")
        }
    }
}
