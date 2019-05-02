package com.limelight.limelight.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.limelight.limelight.R;
import com.limelight.limelight.adapters.FeedAdapter;
import com.limelight.limelight.adapters.KeywordAdapter;
import com.limelight.limelight.adapters.TopicAdapter;
import com.limelight.limelight.core.RetrofitClient;
import com.limelight.limelight.listeners.RecyclerViewClickListener;
import com.limelight.limelight.models.Article;
import com.limelight.limelight.models.Category;
import com.limelight.limelight.models.ErrorModel;
import com.limelight.limelight.models.Keyword;
import com.limelight.limelight.network.Api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewSearchActivity extends AppCompatActivity {
    private TextView title;
    private ImageButton backBtn;
    private SharedPreferences sharedPref;
    RelativeLayout keywordsLayout;
    String t = "";
    ArrayList<Article> articlesList;
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView.Adapter mAdapter;
    private String token = "";
    private int page = 1;
    ArrayList<String> keywords;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_search);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            t = extras.getString("searchQuery");
            title = findViewById(R.id.title);
            title.setText(t);
        }


        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> finish());

        //set the keywords layout to be invisible
        keywordsLayout = findViewById(R.id.keywordsLayout);
        keywordsLayout.setVisibility(View.GONE);

        articlesList = new ArrayList<>();
        keywords = new ArrayList<>();

        swipeContainer = findViewById(R.id.feedContainer);
        RecyclerView recyclerView = findViewById(R.id.feed_recycler);

        swipeContainer.setRefreshing(true);


        sharedPref = getSharedPreferences("limelight", Context.MODE_PRIVATE);
        token = "";
        if (sharedPref.contains("token")) {
            //get token from sharedprefs
            token = "Bearer " + sharedPref.getString("token", "");
            Log.i("token11", token);
        } else {
            //go to login activity
            logout();
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        RecyclerViewClickListener listener = (v, position) -> {
            Intent i = new Intent(ViewSearchActivity.this, ViewArticleActivity.class);
            i.putExtra("url", articlesList.get(position).getLink());
            startActivity(i);
        };

        mAdapter = new FeedAdapter(articlesList, listener);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


        //FOR KEYWORDS
        RecyclerView keysListView = findViewById(R.id.keysListView);

        RecyclerViewClickListener listener2 = (v, position) -> {
            String text = "Would you like to follow " + keywords.get(position) + "?";
            new SweetAlertDialog(this)
                    .setTitleText("Are you sure?")
                    .setContentText(text)
                    .setConfirmText("Confirm")
                    .setCancelButton("No", Dialog::dismiss)
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        follow(keywords.get(position), position);
                    }).show();
        };


        adapter = new KeywordAdapter(keywords, listener2);
        keysListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        keysListView.setAdapter(adapter);
        //keysListView.setAdapter(adapter);

        if (!token.equals("")) {
            loadArticles(token, t, page, this);
            loadKeyWords(token, t, this);

        } else {
            logout();
        }

        swipeContainer.setOnRefreshListener(() -> {
            articlesList.clear();
            loadArticles(token, t, page, this);
        });
    }

    //function to logout
    private void logout() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
        Intent i = new Intent(ViewSearchActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    //function to load articles relevant to the category
    private void loadArticles(String apiKey, String searchStr, int page, Context ctx) {
        Api api = RetrofitClient.getInstance().getApiService();

        Call<ArrayList<Article>> call = api.getFeedBySearch(apiKey, searchStr, page);


        call.enqueue(new Callback<ArrayList<Article>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Article>> call, @NonNull Response<ArrayList<Article>> response) {
                if (response.body() != null && response.isSuccessful()) {
                    articlesList.addAll(response.body());
                    swipeContainer.setRefreshing(false);
                    mAdapter.notifyDataSetChanged();

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
                swipeContainer.setRefreshing(false);

            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Article>> call, @NonNull Throwable t) {
                swipeContainer.setRefreshing(false);
                SweetAlertDialog pDialog = new SweetAlertDialog(ctx, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("No internet connection");
                pDialog.show();
            }

        });
    }


    //function that gets keywords
    private void loadKeyWords(String apiKey, String searchStr, Context ctx) {

        Api api = RetrofitClient.getInstance().getApiService();

        Call<Keyword> call = api.getKeywords(apiKey, searchStr);


        call.enqueue(new Callback<Keyword>() {
            @Override
            public void onResponse(@NonNull Call<Keyword> call, @NonNull Response<Keyword> response) {
                if (response.body() != null && response.isSuccessful()) {
                    keywords.addAll(response.body().getKeywords());
                    if (keywords.size() <= 0) {
                        keywordsLayout.setVisibility(View.GONE);
                    } else {
                        keywordsLayout.setVisibility(View.VISIBLE);
                    }
                    //swipeContainer.setRefreshing(false);
                    //mAdapter.notifyDataSetChanged();

                    Log.i("keywords", keywords.toString());

                }
//                else if (response.errorBody() != null) {
//                    Gson gson = new GsonBuilder().create();
//                    ErrorModel mErrorModel;
//                    try {
//                        mErrorModel = gson.fromJson(response.errorBody().string(), ErrorModel.class);
//                        //Toast.makeText(getApplicationContext(), mErrorModel.getMessage(), Toast.LENGTH_LONG).show();
//                        new SweetAlertDialog(ctx, SweetAlertDialog.ERROR_TYPE)
//                                .setTitleText("Error")
//                                .setContentText(mErrorModel.getMessage())
//                                .show();
//
//                        //logout();
//
//                    } catch (IOException e) {
//                        Toast.makeText(ctx, "An error occurred", Toast.LENGTH_LONG).show();
//                    }
//                } else {
//                    Toast.makeText(ctx, "An error occurred", Toast.LENGTH_LONG).show();
//                }


            }

            @Override
            public void onFailure(@NonNull Call<Keyword> call, @NonNull Throwable t) {

//                SweetAlertDialog pDialog = new SweetAlertDialog(ctx, SweetAlertDialog.ERROR_TYPE)
//                        .setTitleText("Error")
//                        .setContentText("No internet connection");
//                pDialog.show();
            }

        });

    }


    private void follow(String topic, int position) {
        HashMap<String, String> map = new HashMap<>();
        map.put("topicString", topic);
        Api api = RetrofitClient.getInstance().getApiService();
        Call<JsonObject> call = api.addFollow(token, map);


        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.body() != null && response.isSuccessful()) {

                    //enable the button
                    //followButton.setEnabled(true);
                    keywords.remove(position);
                    if (keywords.size() <= 0) {
                        keywordsLayout.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();

                } else if (response.errorBody() != null) {
                    Gson gson = new GsonBuilder().create();
                    ErrorModel mErrorModel;
                    try {
                        mErrorModel = gson.fromJson(response.errorBody().string(), ErrorModel.class);
                        //Toast.makeText(getApplicationContext(), mErrorModel.getMessage(), Toast.LENGTH_LONG).show();
                        new SweetAlertDialog(ViewSearchActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText(mErrorModel.getMessage())
                                .show();


                    } catch (IOException e) {
                        Toast.makeText(ViewSearchActivity.this, "An error occurred", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ViewSearchActivity.this, "An error occurred", Toast.LENGTH_LONG).show();
                }
                //enable the button


            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                //enable the button

                SweetAlertDialog pDialog = new SweetAlertDialog(ViewSearchActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("No internet connection");
                pDialog.show();
            }

        });


    }


}
