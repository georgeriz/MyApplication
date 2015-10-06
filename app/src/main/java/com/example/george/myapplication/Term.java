package com.example.george.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by George on 2015-09-19.
 */
public class Term implements Parcelable{
    private int _id;
    private Word word;
    private Translation translation;
    private int degree;

    public Term(int anId, String aWord, String aTranslation, int aDegree){
        _id = anId;
        word = new Word(aWord);
        translation = new Translation(aTranslation);
        degree = aDegree;
    }

    public int getID() { return _id;}
    public String getWord() { return word.getWord(); }
    public String getTranslation() {
        return translation.getTranslation();
    }
    public int getDegree() { return degree; }

    public void setWord(String aWord) { word.setWord(aWord); }
    public void setTranslation(String aTranslation) { translation.setTranslation(aTranslation); }
    public void setDegree(int aDegree) { degree = aDegree; }

    public boolean checkWord(String userInput) {
        return word.getWord().equals(userInput);
    }

    public boolean checkTranslation(String userInput) {
        return translation.contains(userInput);
    }

    public boolean updateDegree(boolean correctInput) {
        int addition = 0;
        if(correctInput) {
            addition = 1000;
        }
        int newDegree = (degree + addition)/2;
        if(newDegree > 950) {
            newDegree = 1000;
        }
        degree = newDegree;
        return true;
    }

    //for the parcelable
    public Term(Parcel source) {
        String[] data = new String[4];
        source.readStringArray(data);
        _id = Integer.parseInt(data[0]);
        word = new Word(data[1]);
        translation = new Translation(data[2]);
        degree = Integer.parseInt(data[3]);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {String.valueOf(_id), word.getWord(),
                translation.getTranslation(), String.valueOf(degree)});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Term createFromParcel(Parcel source) {
            return new Term(source);
        }

        @Override
        public Term[] newArray(int size) {
            return new Term[size];
        }
    };
}
