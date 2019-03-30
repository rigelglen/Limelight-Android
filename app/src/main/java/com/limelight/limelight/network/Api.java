package com.limelight.limelight.network;

import com.limelight.limelight.models.User;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Api {

    @POST("users/register")
    Call<User> registerUser(@Body HashMap<String, String> body);


}
