package com.limelight.limelight.core;

import com.google.gson.GsonBuilder;
import com.limelight.limelight.BuildConfig;
import com.limelight.limelight.network.Api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static RetrofitClient instance = null;
    private Retrofit retrofit;
    private OkHttpClient client;

    private Api apiService;

    private RetrofitClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(logging);
        }

        client = httpClient.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .client(httpClient.build()).build();

        apiService = retrofit.create(Api.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public Api getApiService() {
        return apiService;
    }


}