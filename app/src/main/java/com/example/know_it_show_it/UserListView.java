package com.example.know_it_show_it;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firestore.v1.DocumentChange;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class UserListView extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView gamePin_navbar_slot;
    private RecyclerView users_list_view;
    private DocumentReference usersGameDocRef;
    private CollectionReference sessionsRef;

    private View LoadingPanel;
    private String[] UserDetailsArray;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        Intent intent = getIntent();
        UserDetailsArray = intent.getStringArrayExtra("details");

        gamePin_navbar_slot = findViewById(R.id.gamePin_navbar_slot);
        gamePin_navbar_slot.setText("#" + UserDetailsArray[0]);

        //getting loader
        LoadingPanel = findViewById(R.id.loadingPanel);
        users_list_view = findViewById(R.id.users_list_view);
        //setting up firebase references
        db = FirebaseFirestore.getInstance();
        usersGameDocRef = db.collection("users").document(UserDetailsArray[0]);
        sessionsRef = db.collection("sessions");
        //getting the data which is now available

        GetData();

        //looking for any data change in DB to get it -> LiveData
        usersGameDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    LoadingPanel.setVisibility(View.VISIBLE);
                    GetData();
                }
            }
        });

        //Looking for the game to start, creating a DB listener on data change
        sessionsRef.document(UserDetailsArray[0]).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Map<String, Object> data = snapshot.getData();

                    if (data.get("active").toString() == "true") {
                        SwitchToMainGame();
                    }
                }
            }
        });

        users_list_view.setLayoutManager(new LinearLayoutManager(this));
    }

    private void GetData() {
        usersGameDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    LoadingPanel.setVisibility(View.GONE);
                    Map<String, Object> data = documentSnapshot.getData();
                    // Create an instance of MyAdapter and pass the data to it
                    MapAdapter adapter = new MapAdapter(data);
                    // Set the adapter to your RecyclerView
                    users_list_view.setAdapter(adapter);
                }
            }
        });
    }
    private void SwitchToMainGame(){
        Intent game = new Intent(this, Game.class);
        game.putExtra("details", this.UserDetailsArray);
        startActivity(game);
    }
    private void SwitchToStart(){
        Intent game_enter = new Intent(this, GameEnter.class);
        startActivity(game_enter);
    }
}