package com.limelight.limelight.models;

public class ClassificationReport {
    private Sentiment sentiment;

    private Clickbait clickbait;

    private Writing writing;

    private String disclaimer;

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public Writing getWriting() {
        return writing;
    }

    public void setWriting(Writing writing) {
        this.writing = writing;
    }

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
