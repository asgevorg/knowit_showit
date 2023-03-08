package com.example.know_it_show_it;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Random;


//todo error in getting game_name_text fix that
public class CreateGame extends AppCompatActivity {
    Button create_game_btn;
    EditText game_name;
    Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        create_game_btn = findViewById(R.id.create_game);
        game_name = (EditText) findViewById(R.id.game_name);

        create_game_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Session session = new Session(game_name.getText().toString(), false);
                if(isNetworkConnectionAvailable()){
                    User user = session.AddSessionToFirestore();
                    switch_to_user_list(user);
                }
            }
        });


//
    }

    private void switch_to_main_game(){
        Intent main_activity = new Intent(this, MainActivity.class);
        startActivity(main_activity);
    }

    private void switch_to_user_list(User user){
        Intent user_list = new Intent(this, AdminUserListView.class);
        user_list.putExtra("details", new String[]{user.getGamePin(), user.getNickname()});
        startActivity(user_list);
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
}