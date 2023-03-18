package com.example.know_it_show_it;

import static android.content.ContentValues.TAG;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public interface UrbanDictionaryCallback {
    void onSuccess(UrbanDictionaryDefinition definition);
    void onError(String errorMessage);
}

class UrbanDictionaryClient {

    private UrbanDictionaryAPIService service;

    public UrbanDictionaryClient() {
        // Create a Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.urbandictionary.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of the API service
        service = retrofit.create(UrbanDictionaryAPIService.class);
    }

    public void getRandomDefinition(String searchTerm, UrbanDictionaryCallback callback) {
        // Make the API call to the "random" endpoint
        Call<UrbanDictionaryResponse> call = service.getRandomDefinition();

        call.enqueue(new Callback<UrbanDictionaryResponse>() {
            @Override
            public void onResponse(Call<UrbanDictionaryResponse> call, Response<UrbanDictionaryResponse> response) {
                if (response.isSuccessful()) {
                    // Get the first definition from the response and use it
                    UrbanDictionaryDefinition definition = response.body().getDefinitions().get(0);

                    // Call the onSuccess callback with the definition
                    callback.onSuccess(definition);
                } else {
                    // Handle the error
                    String errorMessage = "Error: " + response.code();
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<UrbanDictionaryResponse> call, Throwable t) {
                // Handle the error
                String errorMessage = "Error: " + t.getMessage();
                callback.onError(errorMessage);
            }
        });
    }
}
