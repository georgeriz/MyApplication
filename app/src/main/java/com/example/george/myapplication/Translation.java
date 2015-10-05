package com.example.george.myapplication;

/**
 * Created by George on 2015-09-22.
 */
public class Translation {
    private String mainTranslation;


    public Translation(String translation) {
        mainTranslation = translation;
    }

    public String getTranslation() {
        return mainTranslation;
    }

    public void setTranslation(String aTranslation) {
        mainTranslation = aTranslation;
    }
}
