package com.example.know_it_show_it;

import java.util.Random;

public class RandomLetter {
    static public String GenerateRandomLetter(){
        Random r = new Random();
        return String.valueOf((char)(r.nextInt(26)+'a'));

    }
}

