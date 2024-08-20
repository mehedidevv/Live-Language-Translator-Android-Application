package com.example.languagetranslatorapp;

public class MassageModel {

    private String sender,reciver,massage;

    public MassageModel() {
    }

    public MassageModel(String sender, String reciver, String massage) {
        this.sender = sender;
        this.reciver = reciver;
        this.massage = massage;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciver() {
        return reciver;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }
}
