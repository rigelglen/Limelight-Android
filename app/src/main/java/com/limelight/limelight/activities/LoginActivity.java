package com.limelight.limelight.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.limelight.limelight.R;
import com.limelight.limelight.models.ErrorModel;
import com.limelight.limelight.models.User;
import com.limelight.limelight.network.Api;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private AnimationDrawable animationDrawable;
    private Button userLoginButton;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    public static final String BASE_URL = "http://192.168.43.200:4000";
    private Api api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView usernameErrorText = findViewById(R.id.usernameErrorText);
        TextView passwordErrorText = findViewById(R.id.passwordErrorText);
        usernameErrorText.setVisibility(View.GONE);
        passwordErrorText.setVisibility(View.GONE);


        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        RelativeLayout relativeLayout = findViewById(R.id.parentLoginLayout);
        animationDrawable = (AnimationDrawable) relativeLayout.getBackground();

        // setting enter fade animation duration to 5 seconds
        animationDrawable.setEnterFadeDuration(4000);

        // setting exit fade animation duration to 2 seconds
        animationDrawable.setExitFadeDuration(4000);

        //End of animation code

        Button registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .client(httpClient.build()).build();

        api = retrofit.create(Api.class);

        userLoginButton = findViewById(R.id.userLoginButton);

        userLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameErrorText.setVisibility(View.GONE);
                passwordErrorText.setVisibility(View.GONE);

                TextInputEditText userName = findViewById(R.id.userName);
                TextInputEditText userPassword = findViewById(R.id.userPassword);

                String user = userName.getText().toString().trim();
                HashMap<String, String> map = new HashMap<>();


                if (user != null && !user.isEmpty() && isValid(user)) {


                    String pass = userPassword.getText().toString();
                    if (pass != null && !pass.isEmpty()) {

                        map.put("email", user);
                        map.put("password", pass);
                        userLoginButton.setEnabled(false);

                        Call<User> call = api.authenticateUser(map);



                        call.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                                userLoginButton.setEnabled(true);
                                if (response.body() != null && response.isSuccessful()) {
                                    String token = response.body().getToken();
                                    Log.i("abc", token);
                                    sharedPref = getPreferences(Context.MODE_PRIVATE);
                                    editor = sharedPref.edit();
                                    editor.putString("token", token);
                                    editor.apply();
                                    //successful Login hence go to Main activity
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();

                                } else if (response.errorBody() != null) {
                                    Gson gson = new GsonBuilder().create();
                                    ErrorModel mErrorModel;
                                    try {
                                        mErrorModel = gson.fromJson(response.errorBody().string(), ErrorModel.class);
                                        Toast.makeText(getApplicationContext(), mErrorModel.getMessage(), Toast.LENGTH_LONG).show();
                                    } catch (IOException e) {
                                        Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_LONG).show();
                                }


                            }


                            @Override
                            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                                userLoginButton.setEnabled(true);
                                Log.i("abc", "ERROR");
                                Log.i("cdf", t.toString());
                                Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
                            }

                        });


















                    } else {
                        passwordErrorText.setVisibility(View.VISIBLE);
                    }


                } else {
                    usernameErrorText.setVisibility(View.VISIBLE);
                }


            }
        });


    }

    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            // start the animation
            animationDrawable.start();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning()) {
            // stop the animation
            animationDrawable.stop();
        }
    }
}
