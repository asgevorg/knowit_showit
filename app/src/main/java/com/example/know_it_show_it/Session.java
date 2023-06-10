package com.example.know_it_show_it;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Session implements Serializable {
    private String name;
    private String gamePin;
    private boolean isActive;
    private CollectionReference sessionsRef = FirebaseFirestore.getInstance().collection("sessions");
    private CollectionReference questionsRef = FirebaseFirestore.getInstance().collection("questions");

    public Session() {
        //for firestore
    }

    public Session(String name, boolean isActive) {
        this.name = name;
        this.gamePin = generate_token();
        this.isActive = isActive;
    }
    public Session(String name, boolean isActive, String gamePin) {
        this.name = name;
        this.gamePin = gamePin;
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getName() {
        return name;
    }

    public String getGamePin() {
        return gamePin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGamePin(String gamePin) {
        this.gamePin = gamePin;
    }

    public User AddSessionToFirestore() {
        User user = new User("admin", getGamePin(), "admin", (long) 0);
        sessionsRef.document(getGamePin()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        setGamePin(generate_token());
                        AddSessionToFirestore();
                    }else{
                        sessionsRef.document(getGamePin())
                                .set(new Session(getName(), isActive(), getGamePin()));

                        Map<String, String> randomObjectMap = new HashMap<>();
                        randomObjectMap.put("id", "null");
                        randomObjectMap.put("text", "null");
                        randomObjectMap.put("correctAnswer", "null");
                        Map<String, Object> update = new HashMap<>();
                        update.put("randomQuestion", randomObjectMap);

                        questionsRef.document(getGamePin()).set(update);
                        user.push_to_DB();
                    }
                }

            }
        });
        return user;
    }

    private String generate_token(){
        Random rand = new Random();
        String game_id = String.format("%04d", rand.nextInt(10000));
        return game_id;
    }
}
