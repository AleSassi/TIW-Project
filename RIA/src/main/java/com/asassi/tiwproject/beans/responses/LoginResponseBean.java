package com.asassi.tiwproject.beans.responses;

public class LoginResponseBean {

    private String usernameError;
    private String passwordError;

    public LoginResponseBean() {
    }

    public String getUsernameError() {
        return usernameError;
    }

    public String getPasswordError() {
        return passwordError;
    }

    public void setUsernameError(String usernameError) {
        this.usernameError = usernameError;
    }

    public void setPasswordError(String passwordError) {
        this.passwordError = passwordError;
    }
}
