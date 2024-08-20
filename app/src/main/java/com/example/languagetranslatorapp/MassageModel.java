package com.example.languagetranslatorapp;

public class MassageModel {

    private String sender,reciver,massage;
    private long timestamp;

    // Empty Constructor
    public MassageModel() {
    }

    // Constructor With parameter
    public MassageModel(String sender, String reciver, String massage,long timestamp) {
        this.sender = sender;
        this.reciver = reciver;
        this.massage = massage;
        this.timestamp = timestamp;
    }

    // Setter And Getter.......
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
