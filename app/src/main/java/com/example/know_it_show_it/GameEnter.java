package com.example.know_it_show_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class GameEnter extends AppCompatActivity {
    private FirebaseFirestore db;
    private EditText enter_game;
    private EditText nickname;
    private CollectionReference usersRef;
    private CollectionReference sessionsRef;

    private View LoadingPanel = findViewById(R.id.loadingPanel);
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_enter);
        //Loading panel settings
        LoadingPanel.setVisibility(View.GONE);
        ViewCompat.setTranslationZ(findViewById(R.id.loadingPanel), 1);

        //creating fireStore collection instances, asenq enqan arag em sksel havaqel vorovhetev lavna keyboards
        usersRef = FirebaseFirestore.getInstance().collection("users");
        sessionsRef = FirebaseFirestore.getInstance().collection("sessions");
        //Entering the game
        //getting all needed elements of the page
        enter_game = findViewById(R.id.game_pin);
        nickname = findViewById(R.id.nickname);
        Button join_game = findViewById(R.id.join_game);

        //click listener on join game button
        join_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //turning on the loading panel for better UX
                LoadingPanel.setVisibility(View.VISIBLE);
                //getting the gamePin from inside of enter_game editText
                String gamePin = enter_game.getText().toString();
                String nickname_text = nickname.getText().toString();

                if(isNetworkConnectionAvailable()){
                    //checking whether it is null or not
                    if (!gamePin.isEmpty() && !nickname_text.isEmpty()) {
                        //creating a new us=er
                        User newUser = new User(nickname_text, gamePin, "user");
                        //checking if the session exists to continue entering
                        checkSession(newUser);
                        //turning off the loading panel
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    } else {
                        enter_game.setError("Invalid pin");
                        enter_game.setHint("Can't be empty");
                    }
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
    private void switch_to_create_game() {
        Intent create_game_activity = new Intent(this, CreateGame.class);
        startActivity(create_game_activity);
    }

    private void switch_user_list(User user) {
        Intent user_list = new Intent(this, users_list.class);
        user_list.putExtra("details", new String[]{user.getGamePin(), user.getNickname()});
        startActivity(user_list);
    }

    private void checkSession(User user) {
        sessionsRef.document(user.getGamePin()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Document exists, process the data
                            enter_game(user);
                        } else {
                            // Document does not exist
                            enter_game.setError("No such game found");
                        }
                    }
                });
    }

    private void enter_game(User user) {
        this.usersRef.document(user.getGamePin()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (!documentSnapshot.exists()) {
                    } else {
                        DocumentSnapshot data = task.getResult();
                        if (data.exists()) {
                            if (!data.contains(user.getNickname())) {
                                user.push_to_DB();
                                switch_user_list(user);
                            }
                            //Nickname found in DB session
                            else {
                                nickname.setError("Nickname already exists");
                            }
                        }
                        //no gamePin in users list
                        else {

                        }
                    }
                }
            }
        });
    }

    public void checkNetworkConnection(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No internet Connection");
        builder.setMessage("Please check your internet connection");
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public boolean isNetworkConnectionAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if(isConnected) {
            Log.d("Network", "Connected");
            return true;
        }
        else{
            checkNetworkConnection();
            Log.d("Network","Not Connected");
            return false;
        }
    }
}