package com.example.know_it_show_it;

import java.util.Locale;

public class EnglishWordChecker {
    public static boolean isEnglishWord(String word) {
        return word.matches("[a-zA-Z]+") && (Locale.getDefault().getLanguage().equals("en") || word.matches("[a-zA-Z]+('[a-zA-Z]+)?"));
    }
}
