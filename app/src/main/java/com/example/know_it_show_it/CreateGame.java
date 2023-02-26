package com.example.know_it_show_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private CollectionReference sessionsRef;
    CollectionReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        create_game_btn = findViewById(R.id.create_game);
        game_name = (EditText) findViewById(R.id.game_name);

        sessionsRef = FirebaseFirestore.getInstance().collection("sessions");
        usersRef = FirebaseFirestore.getInstance().collection("users");

        create_game_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gamePin = generate_token();
                User user = new User("Admin", gamePin, "admin");
                Session session = new Session(game_name.getText().toString(), gamePin);

                addSessionToFirestore(session);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //deleting the game session from the firestore
    }

    // OPTION AI checking the answers !
        //TODO public or private game sessions, with others joining in from main page with available game list
        //TODO friend request, registration, verification with mail, facebook
    //TODO live players showing
    //TODO Default settings, avatar showing
    //TODO Notification on new game created by friend
    //TODO settings
    //TODO Inviting friends to gamE THROUGHOUT game enterinG
        //TODO on public and private games game entering with approval or not
    //TODO game idea -
    //words are approved by AI Chaquopy - python plugin which is compatible with Java SDK's
    //player rating list

    private String generate_token(){
        String game_id = String.format("%04d", rand.nextInt(10000));
        return game_id;
    }

    private void switch_to_main_game(){
        Intent main_activity = new Intent(this, MainActivity.class);
        startActivity(main_activity);
    }

    private void switch_to_user_list(User user){
        Intent user_list = new Intent(this, users_list.class);
        user_list.putExtra("user", user);
        startActivity(user_list);
    }

    private void addSessionToFirestore(Session session) {
        sessionsRef.document(session.getGamePin()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        session.setGamePin(generate_token());
                        addSessionToFirestore(session);
                    }else{
                        sessionsRef.document(session.getGamePin())
                                .set(session);
                        User user = new User("admin", session.getGamePin(), "admin");
//                        usersRef.document(user)
                        switch_to_user_list(user);
//                        switch_to_main_game();
                    }
                }
            }
        });
    }
}