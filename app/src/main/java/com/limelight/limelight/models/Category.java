package com.limelight.limelight.models;

import java.util.ArrayList;

public class Category {
    private ArrayList<Article> articles;
    private boolean isFollowing;

    public Category(ArrayList<Article> articles, boolean isFollowing) {
        this.articles = articles;
        this.isFollowing = isFollowing;
    }

    public ArrayList<Article> getArticles() {
        return articles;
    }

    public void setArticles(ArrayList<Article> articles) {
        this.articles = articles;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }
}
