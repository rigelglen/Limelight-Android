package com.limelight.limelight.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.limelight.limelight.R;
import com.limelight.limelight.adapters.FeedAdapter;
import com.limelight.limelight.core.RetrofitClient;
import com.limelight.limelight.listeners.RecyclerViewClickListener;
import com.limelight.limelight.models.Article;
import com.limelight.limelight.models.Category;
import com.limelight.limelight.models.ErrorModel;
import com.limelight.limelight.network.Api;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewCategoryActivity extends AppCompatActivity {
    private TextView title;
    private ImageButton backBtn;
    private Button followButton;
    private SharedPreferences sharedPref;
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView.Adapter mAdapter;
    private int page = 1;
    String token;
    String titleText;
    ArrayList<Article> articlesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_category);
        titleText = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            titleText = extras.getString("cat");
            title = findViewById(R.id.title);
            title.setText(titleText);
        }

        followButton = findViewById(R.id.followButton);
        followButton.setVisibility(View.GONE);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> finish());

        articlesList = new ArrayList<>();

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
            Intent i = new Intent(ViewCategoryActivity.this, ViewArticleActivity.class);
            i.putExtra("url", articlesList.get(position).getLink());
            startActivity(i);
        };

        mAdapter = new FeedAdapter(articlesList, listener);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


        if (!token.equals("") && token != null) {
            loadArticles(token, titleText, page, this);

        } else {
            logout();
        }


        swipeContainer.setOnRefreshListener(() -> {
            articlesList.clear();
            loadArticles(token, titleText, page, this);
        });


//        if(category!=null){
//            mAdapter=new FeedAdapter(category.getArticles(),);
//
//            mAdapter.notifyDataSetChanged();
//            swipeContainer.setRefreshing(false);
//        }


    }


    //function to logout
    private void logout() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
        Intent i = new Intent(ViewCategoryActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    //function to load articles relevant to the category
    private void loadArticles(String apiKey, String cat, int page, Context ctx) {
        Api api = RetrofitClient.getInstance().getApiService();

        Call<Category> call = api.getFeedByCategory(apiKey, cat, page);


        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(@NonNull Call<Category> call, @NonNull Response<Category> response) {
                if (response.body() != null && response.isSuccessful()) {
                    articlesList.addAll(response.body().getArticles());
//                    category = new Category(response.body().getArticles(), response.body().isFollowing());
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


            }

            @Override
            public void onFailure(@NonNull Call<Category> call, @NonNull Throwable t) {

                SweetAlertDialog pDialog = new SweetAlertDialog(ctx, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("No internet connection");
                pDialog.show();
            }

        });
    }


}
