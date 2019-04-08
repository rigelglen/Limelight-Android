package com.limelight.limelight.models;

public class ClassificationReport {
    private Sentiment sentiment;

    private Clickbait clickbait;

    public Sentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;
    }

    public Clickbait getClickbait() {
        return clickbait;
    }

    public void setClickbait(Clickbait clickbait) {
        this.clickbait = clickbait;
    }

}
