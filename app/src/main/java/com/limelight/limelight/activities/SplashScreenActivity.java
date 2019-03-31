package com.limelight.limelight.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.limelight.limelight.R;

public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    private SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        /*
         * Showing splash screen with a timer. This will be useful when you
         * want to show case your app logo / company
         */
        new Handler().postDelayed(() -> {
            // This method will be executed once the timer is over
            // Start your app main activity
            if(sharedPref.contains("token")){
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            } else {
                Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);


    }
}
