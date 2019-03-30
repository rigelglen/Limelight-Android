package com.limelight.limelight.models;

public class ErrorModel {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ErrorModel(String message) {
        this.message = message;
    }
}
