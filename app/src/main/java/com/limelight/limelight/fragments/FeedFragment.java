package com.limelight.limelight.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.limelight.limelight.R;
import com.limelight.limelight.activities.LoginActivity;
import com.limelight.limelight.activities.ViewArticleActivity;
import com.limelight.limelight.adapters.FeedAdapter;
import com.limelight.limelight.listeners.RecyclerViewClickListener;
import com.limelight.limelight.models.Article;
import com.limelight.limelight.viewmodel.FeedViewModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class FeedFragment extends Fragment {
    public static final String PARAM = "feed_param";
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Article> feedArticles;
    private SharedPreferences sharedPref;
    private SwipeRefreshLayout swipeContainer;

    private int page = 1;
    private String token = "";
    private FeedViewModel model;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(FeedViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        feedArticles = new ArrayList<>();

        swipeContainer = view.findViewById(R.id.feedContainer);
        RecyclerView recyclerView = view.findViewById(R.id.feed_recycler);

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

        model.getArticles(token, page, getActivity(), false, false).observe(this, articleList -> {
            feedArticles.clear();
            if (articleList != null)
                feedArticles.addAll(articleList);
            mAdapter.notifyDataSetChanged();
            swipeContainer.setRefreshing(false);
        });

        RecyclerViewClickListener listener = (v, position) -> {
            Intent i = new Intent(getActivity(), ViewArticleActivity.class);
            i.putExtra("url", feedArticles.get(position).getLink());
            startActivity(i);
        };

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new FeedAdapter(feedArticles, listener);
        recyclerView.setAdapter(mAdapter);

        swipeContainer.setOnRefreshListener(() -> {
            model.getArticles(token, page, getActivity(), true, false).observe(this, articleList -> {
                page = 1;
                feedArticles.clear();
                if (articleList != null)
                    feedArticles.addAll(articleList);
                mAdapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            });
        });

        /* TODO Fix scrolling */

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
