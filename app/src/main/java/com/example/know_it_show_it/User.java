package com.example.know_it_show_it;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private String nickname;
    private String gamePin;
    private String role;

    private CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");

    public User(String nickname, String gamePin, String role) {
        this.nickname = nickname;
        this.gamePin = gamePin;
        this.role = role;
    }

    public String getNickname() {
        return nickname;
    }

    public String getGamePin() {
        return this.gamePin;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void push_to_DB(){
        Map<String, User> user_details =  new HashMap<>();
        user_details.put(this.getNickname(), this);

        usersRef.document(getGamePin()).set(user_details, SetOptions.merge());
    }
}
