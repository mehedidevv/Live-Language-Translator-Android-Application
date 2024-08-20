package com.example.languagetranslatorapp;

public class UserModel {
    String userID;
    String userName;
    String userEmail;
    String userPassword;
    String userLanguage;


    // Constructor with parameter
    public UserModel(String userID, String userName, String userEmail, String userPassword,String userLanguage) {
        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userLanguage=userLanguage;
    }


// Setter And getter.........
    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserLanguage() {
        return userLanguage;
    }

    public void setUserLanguage(String userLanguage) {
        this.userLanguage = userLanguage;
    }

    // Empty Constructor
    public UserModel() {
    }
}
