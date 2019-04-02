package com.limelight.limelight.observables;

import com.limelight.limelight.models.Article;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

public class ObservableFeedData extends Observable implements Serializable {

    private ArrayList<Article> feedArticles;

    public void setData(ArrayList<Article> articles) {
        feedArticles.clear();
        feedArticles.addAll(articles);
        this.setChanged();
        this.notifyObservers(feedArticles);
    }
}
