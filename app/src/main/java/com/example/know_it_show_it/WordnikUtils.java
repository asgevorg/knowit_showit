package com.example.know_it_show_it;
import android.media.projection.MediaProjection;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;


public class WordnikUtils {
    private static final String BASE_URL = "https://api.wordnik.com/v4";
    private static final String RANDOM_WORD_PATH = "/words.json/randomWord";
    private static final String RANDOM_DEF_PATH = "/words.json/definitions";

    private static final String API_KEY_PARAM = "api_key";
    private static final String API_KEY_VALUE = "ry8nhtcy60qr04bosu5a9jry7n969eewk18vaqt0hbe6j02an"; // Replace with your actual API key

    public static class RandomWordResponse {
        @SerializedName("word")
        public String word;
    }

    public static String getRandomWord() throws IOException {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + RANDOM_WORD_PATH).newBuilder();
        urlBuilder.addQueryParameter(API_KEY_PARAM, API_KEY_VALUE);
        urlBuilder.addQueryParameter("useCanonical", "true");
        urlBuilder.addQueryParameter("limit", "1");
        urlBuilder.addQueryParameter("partOfSpeech", "noun"); // Replace with the desired part of speech

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        RandomWordResponse randomWordResponse = new Gson().fromJson(response.body().charStream(), RandomWordResponse.class);

        return randomWordResponse.word;
    }

    public static String getRandomDefinition() throws IOException {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + RANDOM_DEF_PATH).newBuilder();
        urlBuilder.addQueryParameter(API_KEY_PARAM, API_KEY_VALUE);
        urlBuilder.addQueryParameter("useCanonical", "true");
        urlBuilder.addQueryParameter("limit", "1");
        urlBuilder.addQueryParameter("partOfSpeech", "noun"); // Replace with the desired part of speech

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        RandomWordResponse randomWordResponse = new Gson().fromJson(response.body().charStream(), RandomWordResponse.class);

        return randomWordResponse.word;
    }

}

