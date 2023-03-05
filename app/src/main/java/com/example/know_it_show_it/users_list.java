package com.example.know_it_show_it;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class users_list extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView gamePin_navbar_slot;
    private RecyclerView users_list_view;
    private DocumentReference usersGameDocRef;
    private CollectionReference sessionsRef;
    FirestoreRecyclerAdapter adapter;
    FirestoreRecyclerAdapter adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        Intent intent = getIntent();
        String []details = intent.getStringArrayExtra("details");

        gamePin_navbar_slot = findViewById(R.id.gamePin_navbar_slot);
        gamePin_navbar_slot.setText("#" + details[0]);

        users_list_view = findViewById(R.id.users_list_view);

        db = FirebaseFirestore.getInstance();
        usersGameDocRef = db.collection("users").document(details[0]);

        usersGameDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> data = documentSnapshot.getData();
                    Log.i("MAPADAPTER", data.toString());
                    // Create an instance of MyAdapter and pass the data to it
                    mapAdapter adapter = new mapAdapter(data);
                    // Set the adapter to your RecyclerView
                    users_list_view.setAdapter(adapter);
                }
            }
        });

        users_list_view.setHasFixedSize(true);
        users_list_view.setLayoutManager(new LinearLayoutManager(this));
    }
}