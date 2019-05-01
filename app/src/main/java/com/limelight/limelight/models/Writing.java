package com.limelight.limelight.models;

public class Writing {
    private float real;
    private float fake;

    public Writing(float real, float fake) {
        this.real = real;
        this.fake = fake;
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
}
