package com.asassi.tiwproject.beans;

public class UserBean {

    private String username;
    private String passwordHash;
    private String email;

    public UserBean() {
        username = "";
        passwordHash = "";
        email = "";
    }

    public UserBean(String username, String passwordHash, String email) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
