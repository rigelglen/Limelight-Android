package com.limelight.limelight.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.limelight.limelight.R;
import com.limelight.limelight.activities.ViewCategoryActivity;
import com.limelight.limelight.adapters.CategoryAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FollowFragment extends Fragment {
    private ArrayList<String> suggestedTopics;
    private ListView catListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_follow, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //world nation bussiness technology entertainment sports science health
        suggestedTopics = new ArrayList<>();
        suggestedTopics.add("World");
        suggestedTopics.add("Nation");
        suggestedTopics.add("Business");
        suggestedTopics.add("Technology");
        suggestedTopics.add("Entertainment");
        suggestedTopics.add("Sports");
        suggestedTopics.add("Science");
        suggestedTopics.add("Health");

        catListView = view.findViewById(R.id.catListView);

        CategoryAdapter customAdapter = new CategoryAdapter(getContext(), suggestedTopics);
        catListView.setAdapter(customAdapter);

        catListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Intent i = new Intent(getActivity(), ViewCategoryActivity.class);
                i.putExtra("cat", suggestedTopics.get(position));

                startActivity(i);
            }
        });




    }
}
