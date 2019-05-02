package com.limelight.limelight.models;

public class Writing {
    private float real;
    private float fake;
    private String message;

    public Writing(float real, float fake, String str) {
        this.real = real;
        this.fake = fake;
        this.message = str;
    }

    public float getReal() {
        return real;
    }

    public void setReal(float real) {
        this.real = real;
    }

    public float getFake() {
        return fake;
    }

    public void setFake(float fake) {
        this.fake = fake;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
