package com.example.know_it_show_it;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameEnter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_enter);

        TextView create_game = findViewById(R.id.create_new_game_btn);
        create_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch_to_create_game();
            }
        });
    }

    private void switch_to_create_game(){
        Intent create_game_activity = new Intent(this, CreateGame.class);
        startActivity(create_game_activity);
    }
}