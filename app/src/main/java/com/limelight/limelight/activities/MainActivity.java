package com.limelight.limelight.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.limelight.limelight.core.RetrofitClient;
import com.limelight.limelight.fragments.ClassifyFragment;
import com.limelight.limelight.fragments.FeedFragment;
import com.limelight.limelight.R;
import com.limelight.limelight.fragments.FollowFragment;
import com.limelight.limelight.fragments.HeadlineFragment;
import com.limelight.limelight.models.Article;
import com.limelight.limelight.models.ErrorModel;
import com.limelight.limelight.network.Api;
import com.limelight.limelight.viewmodel.FeedViewModel;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AHBottomNavigation bottomNavigation;
    Fragment fragment;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private int feedPage = 1;



    ArrayList<Article> feedArticles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.ic_check, R.color.color_grey);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.ic_check, R.color.color_grey);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.ic_check, R.color.color_grey);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.tab_4, R.drawable.ic_check, R.color.color_grey);


        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);

        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));
        // Disable the translation inside the CoordinatorLayout
        bottomNavigation.setBehaviorTranslationEnabled(false);
        // Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
        // Force to tint the drawable (useful for font with icon for example)
        bottomNavigation.setForceTint(true);

        // Manage titles
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        // Set current item programmatically

        String token = "";
        sharedPref = getSharedPreferences("limelight", Context.MODE_PRIVATE);

        if (sharedPref.contains("token")) {
            //get token from sharedprefs
            token = "Bearer " + sharedPref.getString("token", "");
            Log.i("token11", token);
        } else {
            //go to login activity
            logout();
        }


//        FeedViewModel model = ViewModelProviders.of(this).get(FeedViewModel.class);
////
////        model.getArticles(token, feedPage, MainActivity.this).observe(this, articleList -> {
////            feedArticles.clear();
////            feedArticles.addAll(articleList);
////        });


        //Fragment fragment = null;
        bottomNavigation.setOnTabSelectedListener((position, wasSelected) -> {
            switch (position) {
                case 0:
                    fragment = new FeedFragment();
                    break;
                case 1:
                    fragment = new HeadlineFragment();
                    break;
                case 2:

                    fragment = new ClassifyFragment();
                    break;
                case 3:
                    fragment = new FollowFragment();
                    break;
                default:
                    break;
            }
            return loadFragment(fragment);
        });

        bottomNavigation.setCurrentItem(0);

    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    public void logout() {
        editor = sharedPref.edit();
        editor.clear();
        editor.apply();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }


}
