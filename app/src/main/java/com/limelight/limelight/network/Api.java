package com.limelight.limelight.network;

import com.google.gson.JsonObject;
import com.limelight.limelight.models.Article;
import com.limelight.limelight.models.Category;
import com.limelight.limelight.models.ClassificationReport;
import com.limelight.limelight.models.Keyword;
import com.limelight.limelight.models.Topic;
import com.limelight.limelight.models.User;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    @POST("users/register")
    Call<User> registerUser(@Body HashMap<String, String> body);

    @POST("users/authenticate")
    Call<User> authenticateUser(@Body HashMap<String, String> body);

    @GET("feed/getFeed")
    Call<ArrayList<Article>> getFeed(@Header("Authorization") String token, @Query("page") int page);

    @GET("topic/getFollows")
    Call<ArrayList<Topic>> getFollows(@Header("Authorization") String token);

    @POST("topic/removeFollow")
    Call<JsonObject> removeFollow(@Header("Authorization") String token, @Body HashMap<String, String> body);

    @POST("topic/addFollow")
    Call<JsonObject> addFollow(@Header("Authorization") String token, @Body HashMap<String, String> body);

    @GET("feed/getFeedByCategory")
    Call<Category> getFeedByCategory(@Header("Authorization") String token, @Query("categoryString") String categoryString, @Query("page") int page);

    @GET("feed/getFeedBySearch")
    Call<ArrayList<Article>> getFeedBySearch(@Header("Authorization") String token, @Query("searchString") String searchString, @Query("page") int page);

    @GET("ml/getKeywords")
    Call<Keyword> getKeywords(@Header("Authorization") String token, @Query("text") String searchString);

    @GET("ml/getClassification")
    Call<ClassificationReport> getClassificationReport(@Header("Authorization") String token, @Query("url") String url);

}
