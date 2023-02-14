package com.example.know_it_show_it;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


//todo error in getting game_name_text fix that
public class CreateGame extends AppCompatActivity {
    Button create_game_btn;
    EditText game_name;
    Random rand = new Random();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        create_game_btn = findViewById(R.id.create_game);
        game_name = (EditText) findViewById(R.id.game_name);
        db = FirebaseFirestore.getInstance();
        Map<String, Object> game = new HashMap<>();
        game.put("Title", game_name.getText().toString());

        create_game_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("games").document(generate_token()).set(game);
            }
        });
    }

    private String generate_token(){
        String game_id = String.format("%04d", rand.nextInt(10000));
        return game_id;
    }

    private void switch_to_main_game(){
        Intent main_activity = new Intent(this, MainActivity.class);
        startActivity(main_activity);
    }
}