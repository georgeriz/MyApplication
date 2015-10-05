package com.example.george.myapplication;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by George on 2015-09-22.
 */
public class Translation {
    ArrayList<String> translations;

    public Translation(String translation) {
        translations = new ArrayList<String>();
        String[] parts = translation.split(",");
        for(String part: parts) {
            translations.add(part.trim());
        }
    }

    public String getTranslation() {
        return TextUtils.join(", ", translations);
    }

    public void setTranslation(String aTranslation) {
        translations.clear();
        String[] parts = aTranslation.split(",");
        for(String part: parts) {
            translations.add(part.trim());
        }
    }

    public boolean contains(String aTranslation) {
        String[] parts = aTranslation.split(",");
        for(String part:parts) {
            if(translations.contains(part.trim())) {
                return true;
            }
        }
        return false;
    }
}
