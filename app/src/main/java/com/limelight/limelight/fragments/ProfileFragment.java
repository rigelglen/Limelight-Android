package com.limelight.limelight.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.limelight.limelight.R;
import com.limelight.limelight.activities.LoginActivity;
import com.limelight.limelight.adapters.TopicAdapter;
import com.limelight.limelight.core.RetrofitClient;
import com.limelight.limelight.models.ErrorModel;
import com.limelight.limelight.models.Topic;
import com.limelight.limelight.network.Api;
import com.limelight.limelight.viewmodel.TopicViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private Button logoutButton;
    private SharedPreferences sharedPref;
    private TopicViewModel model;
    private String token = "";
    private ArrayList<Topic> topics = new ArrayList<>();
    private TopicAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(TopicViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sharedPref = getActivity().getSharedPreferences("limelight", Context.MODE_PRIVATE);

        if (sharedPref.contains("token")) {
            //get token from sharedprefs
            token = "Bearer " + sharedPref.getString("token", "");
            Log.i("token11", token);
        } else {
            //go to login activity
            logout();
        }
        model.getTopics(token, getActivity(), true);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logoutButton = view.findViewById(R.id.logoutButton);
        //logic for logout
        logoutButton.setOnClickListener(v -> new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)

                .setContentText("Are you sure you want to logout?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        logout();
                    }
                })
                .show());

        //logic for adding new follows


        //logic for displaying all follows
        ListView followsListView = view.findViewById(R.id.followsListView);
        if (sharedPref.contains("token")) {
            //get token from sharedprefs
            token = "Bearer " + sharedPref.getString("token", "");
            Log.i("token11", token);
        } else {
            //go to login activity
            logout();
        }
        adapter = new TopicAdapter(topics, getContext());

        model.getTopics(token, getActivity(), false).observe(this, topicList -> {
            topics.clear();
            topics.addAll(topicList);
            adapter.notifyDataSetChanged();

        });

        followsListView.setAdapter(adapter);
        //followsListView.setAdapter(arrayAdapter);
        followsListView.setOnItemClickListener((parent, v, position, id) -> {
            String text = "Are you sure you want to remove '" + topics.get(position).getName() + "' from your follows?";


            new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure?")
                    .setContentText(text)
                    .setConfirmText("Confirm")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            //make network request to remove the item


                            HashMap<String, String> map = new HashMap<>();
                            map.put("topicId", topics.get(position).getId());
                            if (sharedPref.contains("token")) {
                                //get token from sharedprefs
                                token = "Bearer " + sharedPref.getString("token", "");
                                Log.i("token11", token);
                            } else {
                                //go to login activity
                                logout();
                            }
                            Api api = RetrofitClient.getInstance().getApiService();
                            Call<JsonObject> call = api.removeFollow(token, map);


                            call.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                                    if (response.body() != null && response.isSuccessful()) {

                                        topics.remove(position);
                                        model.getTopics(token, getActivity(), true);
                                        adapter.notifyDataSetChanged();


                                    } else if (response.errorBody() != null) {
                                        Gson gson = new GsonBuilder().create();
                                        ErrorModel mErrorModel;
                                        try {
                                            mErrorModel = gson.fromJson(response.errorBody().string(), ErrorModel.class);
                                            //Toast.makeText(getApplicationContext(), mErrorModel.getMessage(), Toast.LENGTH_LONG).show();
                                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Error")
                                                    .setContentText(mErrorModel.getMessage())
                                                    .show();

                                            //logout();

                                        } catch (IOException e) {
                                            Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_LONG).show();
                                    }


                                }

                                @Override
                                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                                    SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Error")
                                            .setContentText("No internet connection");
                                    pDialog.show();
                                }

                            });


                        }
                    })
                    .show();


        });

    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
        Intent i = new Intent(getActivity(), LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        getActivity().finish();
    }
}
