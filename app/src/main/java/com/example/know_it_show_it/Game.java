package com.example.know_it_show_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import net.didion.jwnl.dictionary.Dictionary;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
public class Game extends AppCompatActivity{
    private String[] UserDetailsArray;

    private CollectionReference sessionsRef = FirebaseFirestore.getInstance().collection("sessions");
    private CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");

    private long timeLeftInMillis = 2000;
    private TextView LetterText;

    private ProgressBar loadingBar;
    private EditText answerEditText;

    private Button submitAnswer;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState){
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

        //getting loading bar
        loadingBar = findViewById(R.id.loading_bar);
        loadingBar.setVisibility(View.GONE);

        //Getting the answer EditText
        answerEditText = findViewById(R.id.answer);
        answerEditText.setVisibility(View.GONE);
        //Getting Button for submitAnswer
        submitAnswer = findViewById(R.id.submitAnswer);
        submitAnswer.setVisibility(View.GONE);



        sessionsRef.document(UserDetailsArray[0]).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Document exists, process the data
                            String userTurn = documentSnapshot.getString("player_turn");
                            if(!Objects.equals(userTurn, UserDetailsArray[1])){
                                viewFlipper.setVisibility(View.GONE);
                            }else{
                                viewFlipper.setVisibility(View.VISIBLE);
                            }
                        } else {
                            // Document does not exist

                        }
                    }
                });
        submitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answerEditText.getText() != null){
                    if(EnglishWordChecker.isEnglishWord(answerEditText.getText().toString())){
                        LetterText.setVisibility(View.GONE);
                        answerEditText.setVisibility(View.GONE);
                        submitAnswer.setVisibility(View.GONE);
                    }
                }
            }
        });


        //setting event for flipping animation
        frontLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Generate a random integer between 0 and 25
                String rand = RandomLetter.GenerateRandomLetter();
                Toast.makeText(getApplicationContext(),rand, Toast.LENGTH_SHORT).show();

                viewFlipper.showNext();
                LetterText.setText(rand);
                LetterText.setVisibility(View.VISIBLE);
                answerEditText.setVisibility(View.VISIBLE);
                submitAnswer.setVisibility(View.VISIBLE);
//                startTimer();
            }
        });

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
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
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

    private void startTimer() {
        CountDownTimer timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                LetterText.setText("DONE !");
            }
        }.start();
    }

    // Call this method to update the TextView with the remaining time
    private void updateTimerText() {
        int seconds = (int) (timeLeftInMillis / 1000);
        loadingBar.setProgress(seconds);
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d", seconds);
        LetterText.setText(timeLeftFormatted);
    }

}