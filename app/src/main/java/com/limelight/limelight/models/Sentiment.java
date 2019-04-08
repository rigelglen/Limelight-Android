package com.limelight.limelight.models;

public class Sentiment {
    private float negative;

    private float neutral;

    private float positive;

    private float compound;

    public float getNegative() {
        return negative;
    }

    public void setNegative(float negative) {
        this.negative = negative;
    }

    public float getNeutral() {
        return neutral;
    }

    public void setNeutral(float neutral) {
        this.neutral = neutral;
    }

    public float getPositive() {
        return positive;
    }

    public void setPositive(float positive) {
        this.positive = positive;
    }

    public float getCompound() {
        return compound;
    }

    public void setCompound(float compound) {
        this.compound = compound;
    }
}
