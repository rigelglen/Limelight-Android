package com.limelight.limelight.models;

import java.util.ArrayList;

public class Keyword {
    private ArrayList<String> keywords;

    public Keyword(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }
}
