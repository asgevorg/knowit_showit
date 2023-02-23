package com.example.know_it_show_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GameEnter extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_enter);

//        LocalDB localDatabase = new LocalDB();
//        FirestoreDatabase firestoreDatabase = new FirestoreDatabase();
//        localDatabase.setObserver(firestoreDatabase);

        //creating the Firestore instance
        db = FirebaseFirestore.getInstance();
        //getting all needed elements of the page
        EditText enter_game = findViewById(R.id.game_pin);
        Button join_game = findViewById(R.id.join_game);

        //click listener on join game button
        join_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getting the gamePin from inside of enter_game editText
                String gamePin = enter_game.getText().toString();
                //checking whether it is null or not
                if(!gamePin.isEmpty()){
                    join_session(enter_game.getText().toString());
                }
                else{
                    //constructive test
                    enter_game.setError("Invalid pin");
                    enter_game.setHint("Can't be empty");
                }
            }
        });
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
    //Session joining implementation on Firestore DB
    private void join_session(String game_pin){
        db.collection("sessions").document(game_pin).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        user_details();
                    }else{
                        Log.i("TTTTTTT", "not found");
                    }
                }
            }
        });
    }

    //testing purposes
    private void user_details(){
        Intent user_details = new Intent(this, User_details.class);
        startActivity(user_details);
    }
}