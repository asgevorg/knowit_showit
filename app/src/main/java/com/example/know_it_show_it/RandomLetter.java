package com.example.know_it_show_it;

import java.util.Random;

public class RandomLetter {
    public int GenerateRandomLetter(){
        Random random = new Random();

        // Generate a random integer between 0 and 25 (inclusive)
        int randomNumber = random.nextInt(26);

        // Convert the random number to a letter
        return randomNumber;
    }
}
