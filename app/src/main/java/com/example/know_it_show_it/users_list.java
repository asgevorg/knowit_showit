package com.example.know_it_show_it;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;

public class users_list extends AppCompatActivity {
    TextView gamePin_navbar_slot;
    CollectionReference usersRef;
    CollectionReference sessionsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        Intent intent = getIntent();
        String []details = intent.getStringArrayExtra("details");

        gamePin_navbar_slot = findViewById(R.id.gamePin_navbar_slot);
        gamePin_navbar_slot.setText("#" + details[0]);

        //TODO new recyclerview, adapter for user to get info and put them into recycler view
    }
}