package com.limelight.limelight.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.limelight.limelight.R;
import com.limelight.limelight.adapters.FeedAdapter;
import com.limelight.limelight.models.Article;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FeedFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<Article> feedArticles;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_feed, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        feedArticles=new ArrayList<Article>();
        feedArticles.add(new Article("title1 this is a very long title, very ling title, test test", "source1", "desc1", "url1", "imgUrl1",123456));
        feedArticles.add(new Article("title2", "source2", "desc2", "url2", "imgUrl1",123456));


        recyclerView = view.findViewById(R.id.feed_recycler);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new FeedAdapter(feedArticles, getActivity());
        recyclerView.setAdapter(mAdapter);




    }





    //    public static void getFeed()
//    {
//        //make network request to obtain feed
//
//    }




}
