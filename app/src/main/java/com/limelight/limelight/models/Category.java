package com.limelight.limelight.models;

import java.util.ArrayList;

public class Category {
    private ArrayList<Article> articles;
    private boolean isFollow;
    private String id;

    public Category(ArrayList<Article> articles, boolean isFollow, String id) {
        this.articles = articles;
        this.isFollow = isFollow;
        this.id = id;
    }

    public ArrayList<Article> getArticles() {
        return articles;
    }

    public void setArticles(ArrayList<Article> articles) {
        this.articles = articles;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean following) {
        isFollow = following;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
