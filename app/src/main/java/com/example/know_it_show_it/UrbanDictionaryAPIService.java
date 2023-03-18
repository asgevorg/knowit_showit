package com.example.know_it_show_it;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UrbanDictionaryAPIService {
        @GET("define")
        Call<UrbanDictionaryResponse> getDefinitions(@Query("term") String term);

        @GET("v0/random")
        Call<UrbanDictionaryResponse> getRandomDefinition();


}

