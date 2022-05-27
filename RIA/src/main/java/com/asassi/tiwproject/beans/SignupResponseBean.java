package com.asassi.tiwproject.beans;

public class SignupResponseBean extends LoginResponseBean {

    private String emailError;
    private String repeatPasswordError;

    public String getRepeatPasswordError() {
        return repeatPasswordError;
    }

    public String getEmailError() {
        return emailError;
    }

    public void setRepeatPasswordError(String repeatPasswordError) {
        this.repeatPasswordError = repeatPasswordError;
    }

    public void setEmailError(String emailError) {
        this.emailError = emailError;
    }
}
