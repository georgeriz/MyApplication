package com.example.george.myapplication;

import java.util.regex.*;

/**
 * Created by George on 2015-09-22.
 */
public class Word {
    private String word;
    //private String extra;

    public Word(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String aWord) {
        word = aWord;
    }

    public boolean check(String userInput) {
        if (word.equals(userInput))
            return true;
        else if (word.replace("(", "").replace(")","").trim().equals(userInput))
            return true;
        else {
            Matcher m = Pattern.compile("\\([^)]+\\)").matcher(word);
            while (m.find()) {
                Word s = new Word(word.replace(m.group(), "").trim());
                if(s.check(userInput))
                    return true;
                String q = m.group().replace("(", "").replace(")","");
                Word r = new Word(word.replace(m.group(), q));
                if(r.check(userInput))
                    return true;
            }
        }
        return false;
    }
}
