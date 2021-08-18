package org.usth.ict.ulake.user.model;

public class LoginCredential {
    private String userName;
    private String password;

    public LoginCredential() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
