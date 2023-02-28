package com.example.know_it_show_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class users_list extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView gamePin_navbar_slot;
    private RecyclerView users_list_view;
    private CollectionReference usersRef;
    private CollectionReference sessionsRef;
    FirestoreRecyclerAdapter adapter;

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
        DocumentReference query = db.collection("users").document(details[0]);
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();


        adapter = new FirestoreRecyclerAdapter<User, UserViewHolder>(options) {
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
                holder.nickname.setText(model.getNickname());
            }
        };

        users_list_view.setHasFixedSize(true);
        users_list_view.setLayoutManager(new LinearLayoutManager(this));
        users_list_view.setAdapter(adapter);
    }

    private class UserViewHolder extends  RecyclerView.ViewHolder{
        private TextView nickname;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            nickname = itemView.findViewById(R.id.user_list_item_nickname);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}