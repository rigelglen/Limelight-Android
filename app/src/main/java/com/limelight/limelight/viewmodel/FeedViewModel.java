package com.limelight.limelight.viewmodel;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.limelight.limelight.core.RetrofitClient;
import com.limelight.limelight.models.Article;
import com.limelight.limelight.models.ErrorModel;
import com.limelight.limelight.network.Api;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedViewModel extends ViewModel {

    //this is the data that we will fetch asynchronously
    private MutableLiveData<ArrayList<Article>> articleArrayList;

    //we will call this method to get the data
    public LiveData<ArrayList<Article>> getArticles(String apiKey, int page, Context ctx, boolean force, boolean moreData) {
        //if the ArrayList is null
        if (articleArrayList == null || force) {
            if (!moreData)
                articleArrayList = new MutableLiveData<>();
            //we will load it asynchronously from server in this method
            loadArticles(apiKey, page, ctx);
        }

        //finally we will return the ArrayList
        return articleArrayList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadArticles(String apiKey, int page, Context ctx) {
        Api api = RetrofitClient.getInstance().getApiService();

        Call<ArrayList<Article>> call = api.getFeed(apiKey, page);


        call.enqueue(new Callback<ArrayList<Article>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Article>> call, @NonNull Response<ArrayList<Article>> response) {
                if (response.body() != null && response.isSuccessful()) {

                    articleArrayList.setValue(response.body());


                } else if (response.errorBody() != null) {
                    Gson gson = new GsonBuilder().create();
                    ErrorModel mErrorModel;
                    try {
                        mErrorModel = gson.fromJson(response.errorBody().string(), ErrorModel.class);
                        //Toast.makeText(getApplicationContext(), mErrorModel.getMessage(), Toast.LENGTH_LONG).show();
                        new SweetAlertDialog(ctx, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText(mErrorModel.getMessage())
                                .show();
                        articleArrayList.setValue(articleArrayList.getValue());                        //logout();

                    } catch (IOException e) {
                        Toast.makeText(ctx, "An error occurred", Toast.LENGTH_LONG).show();
                        articleArrayList.setValue(articleArrayList.getValue());
                    }
                } else {
                    Toast.makeText(ctx, "An error occurred", Toast.LENGTH_LONG).show();
                    articleArrayList.setValue(articleArrayList.getValue());
                }


            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Article>> call, @NonNull Throwable t) {

                SweetAlertDialog pDialog = new SweetAlertDialog(ctx, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("No internet connection");
                pDialog.show();
                articleArrayList.setValue(articleArrayList.getValue());
            }

        });
    }
}