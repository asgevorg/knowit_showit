package com.example.know_it_show_it;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FirestoreDatabase implements DBObserver {
    private FirebaseFirestore firestore;

    public FirestoreDatabase() {
        firestore = FirebaseFirestore.getInstance();
    }


    @Override
    public void onDatabaseChange(List<User> data) {

    }
}
