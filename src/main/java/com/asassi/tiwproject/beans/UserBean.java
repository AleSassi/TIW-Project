package com.asassi.tiwproject.beans;

public class UserBean {

    private String nameHash;
    private String passwordHash;

    public UserBean() {
        nameHash = "";
        passwordHash = "";
    }

    public UserBean(String nameHash, String passwordHash) {
        this.nameHash = nameHash;
        this.passwordHash = passwordHash;
    }

    public String getNameHash() {
        return nameHash;
    }

    public void setNameHash(String nameHash) {
        this.nameHash = nameHash;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
