package com.example.know_it_show_it;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.know_it_show_it.databinding.LoadingPanelBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.IntStream;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Game extends AppCompatActivity {
    private String[] UserDetailsArray;

    private CollectionReference sessionsRef = FirebaseFirestore.getInstance().collection("sessions");
    private CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");

    private long timeLeftInMillis = 2000;
    private TextView LetterText;

    private ProgressBar loadingBar;
    private EditText answerEditText;

    private Button submitAnswer;
    protected TextView DefinitionText;

    private static final String WORDNIK_API_KEY = "ry8nhtcy60qr04bosu5a9jry7n969eewk18vaqt0hbe6j02an";
    private static final String WORDNIK_API_URL = "https://api.wordnik.com/v4/word.json/";
    private static final String WORDNIK_API_ENDPOINT = "/definitions?limit=1&includeRelated=false&sourceDictionaries=all&useCanonical=false&includeTags=false&api_key=" + WORDNIK_API_KEY;

    private String randomWord;
    private View LoadingPanel;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        LetterText = findViewById(R.id.LetterText);

        Intent intent = getIntent();
        UserDetailsArray = intent.getStringArrayExtra("details");

        //getting/setting the pin for the navbar
        TextView gamePin_navbar_slot = findViewById(R.id.gamePin_navbar_slot);
        gamePin_navbar_slot.setText("#" + UserDetailsArray[0]);

        //Getting sources for card flipping
        ViewFlipper viewFlipper = findViewById(R.id.viewFlipper);
        //Setting up flip animation
        Animation inAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation outAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        viewFlipper.setInAnimation(inAnimation);
        viewFlipper.setOutAnimation(outAnimation);
        // Adding click listener to flip card
        RelativeLayout frontLayout = findViewById(R.id.frontLayout);

        LoadingPanel = findViewById(R.id.loadingPanel);

        //getting loading bar
//        loadingBar = findViewById(R.id.loading_bar);
//        loadingBar.setVisibility(View.GONE);

        //Getting the answer EditText
        answerEditText = findViewById(R.id.answer);
        answerEditText.setVisibility(View.GONE);
        //Getting Button for submitAnswer
        submitAnswer = findViewById(R.id.submitAnswer);
        submitAnswer.setVisibility(View.GONE);
        //getting the top definition text
        DefinitionText = findViewById(R.id.DefinitionText);
        DefinitionText.setVisibility(View.GONE);



        sessionsRef.document(UserDetailsArray[0]).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Document exists, process the data
                            String userTurn = documentSnapshot.getString("player_turn");
                            if (!Objects.equals(userTurn, UserDetailsArray[1])) {
                                viewFlipper.setVisibility(View.GONE);
                                LoadingPanel.setVisibility(View.VISIBLE);
                            } else {
                                LoadingPanel.setVisibility(View.GONE);
                                viewFlipper.setVisibility(View.VISIBLE);
                                new WordnikTask().execute();
                                DefinitionText.setVisibility(View.VISIBLE);
                            }
                        } else {
                            // Document does not exist

                        }
                    }
                });
        submitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerEditText.getText() != null) {
                    if (EnglishWordChecker.isEnglishWord(answerEditText.getText().toString())) {
                        LetterText.setVisibility(View.GONE);
                        answerEditText.setVisibility(View.GONE);
                        submitAnswer.setVisibility(View.GONE);

                        String answer = answerEditText.getText().toString();
                        if(answer.equals(randomWord)){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Game.this);
                                builder.setTitle("Congratulations");
                                builder.setMessage("The answer is right !");
                                builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                        }

                    }
                }
            }
        });


        //setting event for flipping animation
        frontLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Generate a random integer between 0 and 25
//                String rand = RandomLetter.GenerateRandomLetter();
//                Toast.makeText(getApplicationContext(),rand, Toast.LENGTH_SHORT).show();

                viewFlipper.showNext();
                LetterText.setText(String.valueOf(randomWord.charAt(0)));
//                LetterText.setText(rand);
                LetterText.setVisibility(View.VISIBLE);
                answerEditText.setVisibility(View.VISIBLE);
                submitAnswer.setVisibility(View.VISIBLE);
//                startTimer();
            }
        });

    }

    private class WordnikTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                randomWord = getRandomWord();
                URL url = new URL(WORDNIK_API_URL + randomWord + WORDNIK_API_ENDPOINT);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    in.close();

                    JSONArray jsonArray = new JSONArray(response.toString());
                    if (jsonArray.length() > 0) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        return jsonObject.getString("text");
                    } else {
                        return "No definition found";
                    }
                } else {
                    return "Failed to get definition";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            DefinitionText.setText(result + ": :"+randomWord);
        }
    }

    private String getRandomWord() throws Exception {
        URL url = new URL("https://api.wordnik.com/v4/words.json/randomWord?hasDictionaryDef=true&excludePartOfSpeech=proper-noun,proper-noun-plural&minCorpusCount=10000&maxCorpusCount=-1&minDictionaryCount=1&maxDictionaryCount=-1&minLength=5&maxLength=-1&api_key=" + WORDNIK_API_KEY);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            return jsonObject.getString("word");
        } else {
            throw new Exception("Failed to get random word");
        }
    }
}