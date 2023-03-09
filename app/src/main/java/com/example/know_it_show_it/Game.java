package com.example.know_it_show_it;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class Game extends AppCompatActivity {
    private String[] UserDetailsArray;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

//        Intent intent = getIntent();
//        UserDetailsArray = intent.getStringArrayExtra("details");
//        TextView gamePin_navbar_slot = findViewById(R.id.gamePin_navbar_slot);
//        gamePin_navbar_slot.setText("#" + UserDetailsArray[0]);

        ViewFlipper viewFlipper = findViewById(R.id.viewFlipper);
        TextView LetterText = findViewById(R.id.LetterText);

        RandomLetter randomLetter = new RandomLetter();

// Set up flip animation
        Animation inAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation outAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        viewFlipper.setInAnimation(inAnimation);
        viewFlipper.setOutAnimation(outAnimation);

// Add click listener to flip card
        RelativeLayout frontLayout = findViewById(R.id.frontLayout);
        frontLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String randLetter = Character.toString((char) randomLetter.GenerateRandomLetter());
                Toast.makeText(getApplicationContext(),randLetter, Toast.LENGTH_SHORT).show();
                viewFlipper.showNext();
//                LetterText.setText();
                LetterText.setVisibility(View.VISIBLE);
            }
        });
    }
}