package com.example.know_it_show_it;


import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public interface DBObserver {
    void onDatabaseChange(List<User> data);
}
