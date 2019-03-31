package com.limelight.limelight.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.limelight.limelight.R;
import com.limelight.limelight.core.Const;
import com.limelight.limelight.core.RetrofitClient;
import com.limelight.limelight.models.ErrorModel;
import com.limelight.limelight.models.User;
import com.limelight.limelight.network.Api;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import cn.pedant.SweetAlert.SweetAlertDialog;
import kotlin.Unit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private AnimationDrawable animationDrawable;
    private CircularProgressButton userLoginButton;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
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

        //checking if internet connection


        Button registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });

        api = RetrofitClient.getInstance().getApiService();

        userLoginButton = findViewById(R.id.userLoginButton);

        userLoginButton.setOnClickListener(v -> {
            usernameErrorText.setVisibility(View.GONE);
            passwordErrorText.setVisibility(View.GONE);

            TextInputEditText userName = findViewById(R.id.userName);
            TextInputEditText userPassword = findViewById(R.id.userPassword);
            String user = null;
            if(userName.getText()!=null)
                user = userName.getText().toString().trim();
            HashMap<String, String> map = new HashMap<>();


            if (user!=null && !user.isEmpty() && isValid(user)) {
                String pass=null;
                if(userPassword.getText()!=null)
                    pass = userPassword.getText().toString();
                if (pass!=null && !pass.isEmpty()) {

                    map.put("email", user);
                    map.put("password", pass);
                    userLoginButton.setEnabled(false);

                    Call<User> call = api.authenticateUser(map);
                    userLoginButton.startAnimation(()-> Unit.INSTANCE);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                            userLoginButton.setEnabled(true);
                            userLoginButton.doneLoadingAnimation(Color.GREEN, getBitmapFromVectorDrawable(LoginActivity.this, R.drawable.ic_check));
                            if (response.body() != null && response.isSuccessful()) {
                                String token = response.body().getToken();
                                Log.i("abc", token);
                                sharedPref = getSharedPreferences("limelight", Context.MODE_PRIVATE);
                                editor = sharedPref.edit();
                                editor.putString("token", token);
                                editor.apply();
                                //successful Login hence go to Main activity
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();

                            } else if (response.errorBody() != null) {
                                userLoginButton.revertAnimation(()->Unit.INSTANCE);
                                Gson gson = new GsonBuilder().create();
                                ErrorModel mErrorModel;
                                try {
                                    mErrorModel = gson.fromJson(response.errorBody().string(), ErrorModel.class);
                                    //Toast.makeText(getApplicationContext(), mErrorModel.getMessage(), Toast.LENGTH_LONG).show();
                                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Error")
                                            .setContentText(mErrorModel.getMessage())
                                            .show();
                                } catch (IOException e) {
                                    Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_LONG).show();
                            }


                        }


                        @Override
                        public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                            userLoginButton.revertAnimation(()->Unit.INSTANCE);
                            userLoginButton.setEnabled(true);
                            Log.i("abc", "ERROR");
                            Log.i("cdf", t.toString());

                            SweetAlertDialog pDialog=new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error")
                                    .setContentText("No internet connection");
                            pDialog.show();
                        }

                    });




                } else {
                    passwordErrorText.setVisibility(View.VISIBLE);
                }


            } else {
                usernameErrorText.setVisibility(View.VISIBLE);
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

//    public boolean isConnected(Context context) {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivityManager != null) {
//            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//        }
//        return false;
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        assert drawable != null;
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
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
