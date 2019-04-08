package com.limelight.limelight.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.limelight.limelight.R;
import com.limelight.limelight.activities.ViewCategoryActivity;
import com.limelight.limelight.activities.ViewSearchActivity;
import com.limelight.limelight.adapters.CategoryAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FollowFragment extends Fragment {
    private ArrayList<String> suggestedTopics;
    private ListView catListView;
    private ImageButton topicSearch;

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
        topicSearch = view.findViewById(R.id.topicSearch);
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

        catListView.setOnItemClickListener((parent, v, position, id) -> {

            Intent i = new Intent(getActivity(), ViewCategoryActivity.class);
            i.putExtra("cat", suggestedTopics.get(position));

            startActivity(i);
        });


        //Search by string
        topicSearch = view.findViewById(R.id.topicSearch);
        topicSearch.setOnClickListener(v -> {
            TextInputEditText searchString = view.findViewById(R.id.searchString);
            String searchQuery = searchString.getText().toString();
            if (searchQuery.equals("")) {
                hideKeyboard(getActivity());
                Toast.makeText(getContext(), "Invalid Search Query", Toast.LENGTH_SHORT).show();
            } else {
                hideKeyboard(getActivity());
                Intent i = new Intent(getActivity(), ViewSearchActivity.class);
                i.putExtra("searchQuery", searchQuery);
                startActivity(i);
            }
        });


    }

    private static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View f = activity.getCurrentFocus();
        if (null != f && null != f.getWindowToken() && EditText.class.isAssignableFrom(f.getClass()))
            imm.hideSoftInputFromWindow(f.getWindowToken(), 0);
        else
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
