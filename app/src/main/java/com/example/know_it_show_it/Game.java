package com.example.know_it_show_it;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Game extends AppCompatActivity {
    private String[] UserDetailsArray;

    private CollectionReference sessionsRef = FirebaseFirestore.getInstance().collection("sessions");
    private CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");

    private CollectionReference questionsRef = FirebaseFirestore.getInstance().collection("questions");

    private long timeLeftInMillis = 2000;
    private TextView LetterText;

    private ProgressBar loadingBar;
    private EditText answerEditText;

    private Button submitAnswer;
    protected TextView DefinitionText;
    private String randomWord;
    private View LoadingPanel;
    private Map<String, String> randomQuestion;
    private TextInputLayout answerBox;

    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private TextView CurrentPlayer;

    //Getting sources for card flipping


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        CurrentPlayer = findViewById(R.id.CurrentPlayer);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        answerBox = findViewById(R.id.answerBox);

        LetterText = findViewById(R.id.LetterText);

        Intent intent = getIntent();
        UserDetailsArray = intent.getStringArrayExtra("details");

        ViewFlipper viewFlipper = findViewById(R.id.viewFlipper);

        //getting/setting the pin for the navbar
        TextView gamePin_navbar_slot = findViewById(R.id.gamePin_navbar_slot);

        gamePin_navbar_slot.setText("#" + UserDetailsArray[0]);

        //Setting up flip animation
        Animation inAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation outAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        viewFlipper.setInAnimation(inAnimation);
        viewFlipper.setOutAnimation(outAnimation);
        // Adding click listener to flip card
        RelativeLayout frontLayout = findViewById(R.id.frontLayout);

        LoadingPanel = findViewById(R.id.loadingPanel);


        //Getting the answer EditText
        answerEditText = findViewById(R.id.answer);
        answerEditText.setVisibility(View.GONE);
        //Getting Button for submitAnswer
        submitAnswer = findViewById(R.id.submitAnswer);
        submitAnswer.setVisibility(View.GONE);
        //getting the top definition text
        DefinitionText = findViewById(R.id.DefinitionText);
        DefinitionText.setVisibility(View.GONE);

        checkForCurrentPlay();

        sessionsRef.document(UserDetailsArray[0]).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    checkForCurrentPlay();
                }
            }
        });


        submitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerEditText.getText() != null) {
                    String userAnswer = String.valueOf(answerEditText.getText());
                    userAnswer = userAnswer.replaceAll("\\s", "").toLowerCase();

                    String rightAnswer = randomQuestion.get("correctAnswer");
                    rightAnswer = rightAnswer.replaceAll("\\s", "").toLowerCase();

                    LetterText.setVisibility(View.GONE);
                    answerEditText.setVisibility(View.GONE);

                    submitAnswer.setVisibility(View.GONE);

                    if(userAnswer.equals(rightAnswer)){
                            AlertDialog.Builder builder = new AlertDialog.Builder(Game.this);
                            builder.setTitle("Congratulations");
                            builder.setMessage("The answer is right !, +100");
                            builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
                                @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                            });
                                AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            changeUser();
                            addPointToUser(100);
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(Game.this);
                        builder.setTitle("Ohh noo !");
                        builder.setMessage("Do your best the next time )");
                        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        changeUser();
                    }
                }
            }
        });


        //setting event for flipping animation
        frontLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
                LetterText.setText(String.valueOf(randomQuestion.get("correctAnswer").charAt(0)));
                LetterText.setVisibility(View.VISIBLE);
                answerEditText.setVisibility(View.VISIBLE);
                submitAnswer.setVisibility(View.VISIBLE);
            }
        });

    }


    private Map<String, String> getRandomQuestion(){
                try {
                    // Read the JSON file
                    AssetManager assetManager = getAssets();
                    InputStream inputStream = assetManager.open("Questions.json");
                    int size = inputStream.available();
                    byte[] buffer = new byte[size];
                    inputStream.read(buffer);
                    inputStream.close();
                    String json = new String(buffer, StandardCharsets.UTF_8);

                    // Parse the JSON data
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray questionsArray = jsonObject.getJSONArray("questions");

                    // Get a random object from the questions array
                    Random random = new Random();
                    int randomIndex = random.nextInt(questionsArray.length());
                    JSONObject randomObjectArray = questionsArray.getJSONObject(randomIndex);

                    // Extract properties from the random object and store in a HashMap
                    Map<String, String> randomObjectMap = new HashMap<>();
                    for (int i = 0; i < randomObjectArray.length(); i++) {
                        int id = randomObjectArray.getInt("id");
                        String text = randomObjectArray.getString("text");
                        String correctAnswer = randomObjectArray.getString("correctAnswer");

                        // Add the properties to the HashMap
                        randomObjectMap.put("id", Integer.toString(id));
                        randomObjectMap.put("text", text);
                        randomObjectMap.put("correctAnswer", correctAnswer);
                        randomObjectMap.put("currentUser", UserDetailsArray[1]);

                        Map<String, Object> update = new HashMap<>();
                        update.put("randomQuestion", randomObjectMap);

                        questionsRef.document(UserDetailsArray[0]).set(update, SetOptions.merge());
//                        getOtherPlayerInfo();
                    }

                    // Print the HashMap of the random object
                    return randomObjectMap;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Map<String, String> randomObjectMap = new HashMap<>();
                randomObjectMap.put("id", "null");
                randomObjectMap.put("text", "null");
                randomObjectMap.put("correctAnswer", "null");
                return randomObjectMap;
    }
    private void changeUser(){
        sessionsRef.document(UserDetailsArray[0]).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> OrderDetails = (List<String>) document.get("OrderDetails");
                        String playerTurn = document.getString("player_turn");

                        int playerTurnId = OrderDetails.indexOf(playerTurn);

                        if(playerTurnId + 1 <= OrderDetails.toArray().length - 1){
                            playerTurn = OrderDetails.get(playerTurnId + 1);
                        }else{
                            playerTurn = OrderDetails.get(0);
                        }


                        Map<String, Object> updates = new HashMap<>();
                        updates.put("player_turn", playerTurn);

                        sessionsRef.document(UserDetailsArray[0]).update(updates);
                        // Use the parameter value as needed
                    } else {
                        // Document does not exist
                    }
                }else{
                    //fetching error
                }
            }
        });
    }

    private void checkForCurrentPlay(){
        ViewFlipper viewFlipper = findViewById(R.id.viewFlipper);
        Animation inAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation outAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        viewFlipper.setInAnimation(inAnimation);
        viewFlipper.setOutAnimation(outAnimation);

        sessionsRef.document(UserDetailsArray[0]).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Document exists, process the data
                            String userTurn = documentSnapshot.getString("player_turn");
                            if (!Objects.equals(userTurn, UserDetailsArray[1])) {
                                //currently is NOT his TURN
//                                getOtherPlayerInfo();
                                viewFlipper.setVisibility(View.GONE);
                                LoadingPanel.setVisibility(View.GONE); //VISIBLE
                                DefinitionText.setVisibility(View.VISIBLE);
                                answerBox.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                CurrentPlayer.setVisibility(View.VISIBLE);
                                createTheRecyclerView();

                            } else {
                                //Currently is his TURN
                                CurrentPlayer.setVisibility(View.GONE);
                                viewFlipper.setDisplayedChild(0);
                                recyclerView.setVisibility(View.GONE);
                                answerBox.setVisibility(View.VISIBLE);
                                LoadingPanel.setVisibility(View.GONE);
                                viewFlipper.setVisibility(View.VISIBLE);
                                submitAnswer.setVisibility(View.VISIBLE);
                                randomQuestion = getRandomQuestion();
                                DefinitionText.setText(randomQuestion.get("text"));
                                DefinitionText.setVisibility(View.VISIBLE);
                                answerEditText.setText("");
                            }
                        } else {
                            // Document does not exist
                        }
                    }
                });
    }

    private void addPointToUser(int plusScore){
        usersRef.document(UserDetailsArray[0]).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> playerDetails = (Map<String, Object>) document.get(UserDetailsArray[1]);
                        Long currentScore = (Long) playerDetails.get("score");
                        currentScore += plusScore;
                        playerDetails.put("score", currentScore);

                        Map<String, Object> update = new HashMap<>();
                        update.put(UserDetailsArray[1], playerDetails);

                        usersRef.document(UserDetailsArray[0]).update(update);

                    } else {
                        // Document does not exist
                    }
                }else{

                }
            }
        });
    }

    private void createTheRecyclerView(){
        usersRef.document(UserDetailsArray[0]).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                List<User> userList = new ArrayList<>();

                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> playerDetails = (Map<String, Object>) document.getData();

                        for (Map.Entry<String, Object> set : playerDetails.entrySet()) {
                            Map<String, Object> inner = (Map<String, Object>) set.getValue();
                            userList.add(new User(set.getKey(), (Long) inner.get("score")));
                        }

                        //recycler view for not your turn
                        userAdapter = new UserAdapter(userList);
                        recyclerView.setAdapter(userAdapter);

                    } else {
                        // Document does not exist
                    }
                }
            }
        });
    }

    public String[] getOtherPlayerInfo(){
        String[] ExportData = new String[2];
        questionsRef.document(UserDetailsArray[0]).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> questionDetails = (Map<String, Object>) document.getData();
                        Map<String, Object> randomQuestion = (Map<String, Object>) questionDetails.get("randomQuestion");
                        String currentUser = (String) randomQuestion.get("currentUser");
                        String activeQuestion = (String) randomQuestion.get("text");

                        DefinitionText.setText(activeQuestion);
                        CurrentPlayer.setText("Current player: " + currentUser);

                        ExportData[0] = (currentUser);
                        ExportData[1] = activeQuestion;
                    } else {
                        // Document does not exist
                    }
                }

            }
        });
        return ExportData;
    }
}