package com.example.george.myapplication;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.regex.*;

public class Word {
    ArrayList<String> words;

    public Word(String word) {
        words = new ArrayList<>();
        String[] parts = word.split(",");
        for (String part : parts) {
            words.add(part.trim());
        }
    }

    public String getWord() {
        return TextUtils.join(", ", words);
    }

    public void setWord(String word) {
        words.clear();
        String[] parts = word.split(",");
        for (String part : parts) {
            words.add(part.trim());
        }
    }

    public boolean check(String userInput) {
        String[] parts = userInput.split(",");
        for (String part : parts) {
            if (checkPerWord(part.trim()))
                return true;
        }
        return false;
    }

    private boolean checkPerWord(String userInput) {
        if (words.contains(userInput))
            return true;
        else for (String word : words)
            if (checkParenthesis(word, userInput))
                return true;
        return false;
    }

    private boolean checkParenthesis(String word, String userInput) {
        if (word.replace("(", "").replace(")", "").trim().equals(userInput))
            return true;
        else {
            Matcher m = Pattern.compile("\\([^)]+\\)").matcher(word);
            while (m.find()) {
                //Word s = new Word(word.replace(m.group(), "").trim());
                if (checkParenthesis(word.replace(m.group(), "").trim(), userInput))
                    return true;
                String q = m.group().replace("(", "").replace(")", "");
                //Word r = new Word(word.replace(m.group(), q));
                if (checkParenthesis(word.replace(m.group(), q), userInput))
                    return true;
            }
        }
        return false;
    }
}
