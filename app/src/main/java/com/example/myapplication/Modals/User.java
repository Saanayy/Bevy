package com.example.myapplication.Modals;

public class User {
    String userID;
    String email;
    // TODO: Currently storing the password here, remove it later
    String passsword;

    public User(String userID, String email, String passsword) {
        this.userID = userID;
        this.email = email;
        this.passsword = passsword;
    }

    public User() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasssword() {
        return passsword;
    }

    public void setPasssword(String passsword) {
        this.passsword = passsword;
    }
}
