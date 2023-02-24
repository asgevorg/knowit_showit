package com.example.know_it_show_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.animation.core.Animation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GameEnter extends AppCompatActivity {
    private FirebaseFirestore db;
    EditText enter_game;
    EditText nickname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_enter);

        //creating the Firestore instance
        db = FirebaseFirestore.getInstance();

    //Entering the game
        //getting all needed elements of the page
        enter_game = findViewById(R.id.game_pin);
        nickname = findViewById(R.id.nickname);
        Button join_game = findViewById(R.id.join_game);
        Log.i("TTTT", "SS");
        //click listener on join game button
        join_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("TTTT", "Clicked");
                //getting the gamePin from inside of enter_game editText
                String gamePin = enter_game.getText().toString();
                String nickname_text = nickname.getText().toString();
                //checking whether it is null or not
                if(!gamePin.isEmpty() && !nickname_text.isEmpty()){
                    User newUser = new User(nickname_text, gamePin, "001");
                    checkSession(newUser);
                }
                else{
                    enter_game.setError("Invalid pin");
                    enter_game.setHint("Can't be empty");
                }
            }
        });

    //creating game
        //getting needed element
        TextView create_game = findViewById(R.id.create_new_game_btn);
        //switching to create-game page
        create_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch_to_create_game();
            }
        });
    }


    //game page switcher implementation
    private void switch_to_create_game(){
        Intent create_game_activity = new Intent(this, CreateGame.class);
        startActivity(create_game_activity);
    }

    private void checkSession(User user){
        boolean ans = false;
        db.collection("sessions").document(user.getGamePin()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Document exists, process the data
                            Log.i("TTTT", "Enter Access");
                            enter_game(user);
                        } else {
                            Log.i("TTTT", "Failure");
                            // Document does not exist
                            enter_game.setError("");
                            nickname.setError("");
                        }
                    }
                });
    }

    private void enter_game(User user){
        db.collection("users").add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i("TTTT", "Added as " + user.getNickname());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("TTTT", "Failed as " + user.getNickname());
                    }
                });
    }
}