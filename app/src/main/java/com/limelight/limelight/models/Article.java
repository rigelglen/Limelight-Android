package com.limelight.limelight.models;

public class Article {
    private String title;
    private String source;
    private String description;
    private String link;
    private String image;
    private long publishedAt;

    public Article(String title, String source, String desc, String url, String imgUrl, long pubDate) {
        this.title = title;
        this.source = source;
        this.description = desc;
        this.link = url;
        this.image = imgUrl;
        this.publishedAt = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(long publishedAt) {
        this.publishedAt = publishedAt;
    }
}
