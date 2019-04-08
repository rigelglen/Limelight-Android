package com.limelight.limelight.viewmodel;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.limelight.limelight.core.RetrofitClient;
import com.limelight.limelight.models.ErrorModel;
import com.limelight.limelight.models.Topic;
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

public class TopicViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Topic>> topicArrayList;


    //we will call this method to get the data
    public LiveData<ArrayList<Topic>> getTopics(String apiKey, Context ctx, boolean force) {
        //if the ArrayList is null
        if (topicArrayList == null || force) {

            topicArrayList = new MutableLiveData<>();
            //we will load it asynchronously from server in this method
            loadTopics(apiKey, ctx);
        }

        //finally we will return the ArrayList
        return topicArrayList;
    }


    //This method is using Retrofit to get the JSON data from URL
    private void loadTopics(String apiKey, Context ctx) {
        Api api = RetrofitClient.getInstance().getApiService();

        Call<ArrayList<Topic>> call = api.getFollows(apiKey);


        call.enqueue(new Callback<ArrayList<Topic>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Topic>> call, @NonNull Response<ArrayList<Topic>> response) {
                if (response.body() != null && response.isSuccessful()) {

                    topicArrayList.setValue(response.body());


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

                        //logout();

                    } catch (IOException e) {
                        Toast.makeText(ctx, "An error occurred", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ctx, "An error occurred", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Topic>> call, @NonNull Throwable t) {

                SweetAlertDialog pDialog = new SweetAlertDialog(ctx, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("No internet connection");
                pDialog.show();
            }

        });
    }


}
