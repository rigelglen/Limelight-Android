package com.limelight.limelight.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.limelight.limelight.R;
import com.limelight.limelight.activities.LoginActivity;
import com.limelight.limelight.activities.MainActivity;
import com.limelight.limelight.adapters.FeedAdapter;
import com.limelight.limelight.core.RetrofitClient;
import com.limelight.limelight.models.Article;
import com.limelight.limelight.models.ErrorModel;
import com.limelight.limelight.network.Api;
import com.limelight.limelight.viewmodel.FeedViewModel;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedFragment extends Fragment {
    public static final String PARAM = "feed_param";
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Article> feedArticles;
    private SharedPreferences sharedPref;
    private SwipeRefreshLayout swipeContainer;
    int pastVisiblesItems, visibleItemCount, totalItemCount;


    private Api api = RetrofitClient.getInstance().getApiService();
    private int page = 1;
    private String token = "";
    private static boolean loadMore = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_feed, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        feedArticles = new ArrayList<Article>();
        api = RetrofitClient.getInstance().getApiService();

        swipeContainer = view.findViewById(R.id.feedContainer);

        swipeContainer.setRefreshing(true);

        sharedPref = getActivity().getSharedPreferences("limelight", Context.MODE_PRIVATE);

        if (sharedPref.contains("token")) {
            //get token from sharedprefs
            token = "Bearer " + sharedPref.getString("token", "");
            Log.i("token11", token);
        } else {
            //go to login activity
            logout();
        }

        FeedViewModel model = ViewModelProviders.of(getActivity()).get(FeedViewModel.class);

        model.getArticles(token, page, getActivity(), false, false).observe(this, articleList -> {
            feedArticles.clear();
            feedArticles.addAll(articleList);
            mAdapter.notifyDataSetChanged();
            swipeContainer.setRefreshing(false);
        });


        //feedArticles.add(new Article("title1 this is a very long title, very ling title, test test", "source1", "desc1", "url1", "imgUrl1", 123456));
        //feedArticles.add(new Article("title2", "source2", "desc2", "url2", "imgUrl1", 123456));


        RecyclerView recyclerView = view.findViewById(R.id.feed_recycler);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new FeedAdapter(feedArticles, getActivity());
        recyclerView.setAdapter(mAdapter);

        swipeContainer.setOnRefreshListener(() -> {
            model.getArticles(token, page, getActivity(), true, false).observe(this, articleList -> {
                page = 1;
                feedArticles.clear();
                feedArticles.addAll(articleList);
                mAdapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            });
        });

        LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
        // TODO Fix scrolling
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//
//                visibleItemCount = linearLayoutManager.getChildCount();
//                totalItemCount = linearLayoutManager.getItemCount();
//                pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
//
//                if (loadMore) {
//                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
//                        if (page <= 4) {
//                            page++;
//                            loadMore = false;
//                            Toast.makeText(getContext(), "Loading more...", Toast.LENGTH_SHORT).show();
//                            model.getArticles(token, page, getActivity(), true, true).observe(FeedFragment.this, articleList -> {
//                                feedArticles.addAll(articleList);
//                                mAdapter.notifyDataSetChanged();
//                                if(articleList.size()!=0)
//                                    loadMore = true;
//                            });
//                        }
//                    }
//                }
//            }
//        });


    }

    public void getFeed() {
        //make network request to obtain feed
        //HashMap<String,String> map = new HashMap<>();

        String token = "";
        sharedPref = getActivity().getSharedPreferences("limelight", Context.MODE_PRIVATE);

        if (sharedPref.contains("token")) {
            //get token from sharedprefs
            token = "Bearer " + sharedPref.getString("token", "");
            Log.i("token11", token);
        } else {
            //go to login activity
            logout();
        }

        if (!token.equals("")) {

//            HashMap<String, Integer> map = new HashMap<>();
//            map.put("page", page);
            Call<ArrayList<Article>> call = api.getFeed(token, page);


            call.enqueue(new Callback<ArrayList<Article>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<Article>> call, @NonNull Response<ArrayList<Article>> response) {
                    if (response.body() != null && response.isSuccessful()) {

                        //get array of articles
                        feedArticles.clear();
                        feedArticles.addAll(new ArrayList<>(response.body()));
                        //feedArticles=new ArrayList<>(response.body());
                        Log.i("abcd", feedArticles.toString());
                        mAdapter.notifyDataSetChanged();

                    } else if (response.errorBody() != null) {
                        Gson gson = new GsonBuilder().create();
                        ErrorModel mErrorModel;
                        try {
                            mErrorModel = gson.fromJson(response.errorBody().string(), ErrorModel.class);
                            //Toast.makeText(getApplicationContext(), mErrorModel.getMessage(), Toast.LENGTH_LONG).show();
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error")
                                    .setContentText(mErrorModel.getMessage())
                                    .show();

                            //logout();

                        } catch (IOException e) {
                            Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_LONG).show();
                    }


                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<Article>> call, @NonNull Throwable t) {


                    SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("No internet connection");
                    pDialog.show();
                }

            });


        } else {
            //go to login activity
            logout();

        }
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
    }


}
