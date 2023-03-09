package com.example.know_it_show_it;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Map;

public class AdminUserListView extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView gamePin_navbar_slot;
    private RecyclerView users_list_view;
    private DocumentReference usersGameDocRef;
    private CollectionReference sessionsRef;

    private View LoadingPanel;
    private String[] UserDetailsArray;
    private Button GameStart;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_list);

        Intent intent = getIntent();
        UserDetailsArray = intent.getStringArrayExtra("details");

        gamePin_navbar_slot = findViewById(R.id.gamePin_navbar_slot);
        gamePin_navbar_slot.setText("#" + UserDetailsArray[0]);

        //getting loader
        LoadingPanel = findViewById(R.id.loadingPanel);
        users_list_view = findViewById(R.id.users_list_view);
        //getting GameStart button reference
        GameStart = findViewById(R.id.GameStart);

        db = FirebaseFirestore.getInstance();
        usersGameDocRef = db.collection("users").document(UserDetailsArray[0]);
        sessionsRef = db.collection("sessions");

        GetData();
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

        GameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkConnectionAvailable()){
                    sessionsRef.document(UserDetailsArray[0]).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Map<String, Object> SessionData = document.getData();
                                    SessionData.put("active", true);
                                    //getting game into active mode
                                    sessionsRef.document(UserDetailsArray[0]).update(SessionData);
                                    SwitchToMainGame();
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                }

            }
        });

        users_list_view.setLayoutManager(new LinearLayoutManager(this));
    }

    private void GetData() {
        if(isNetworkConnectionAvailable()){
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
    private void SwitchToMainGame(){
        Intent user_list = new Intent(this, Game.class);
        user_list.putExtra("details", this.UserDetailsArray);
        startActivity(user_list);
    }
}