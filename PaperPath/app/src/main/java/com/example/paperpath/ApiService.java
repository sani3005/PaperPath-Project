package com.example.paperpath;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Body;
import retrofit2.http.Path;
import retrofit2.http.PUT;

import java.util.List;

public interface ApiService {

    @GET("api/conferences")
    Call<List<Conference>> getConferences();

    @GET("api/conferences/recommended")
    Call<List<Conference>> getRecommendedConferences(@Query("email") String email);

    @GET("api/conferences/search")
    Call<List<Conference>> searchConferences(@Query("keyword") String keyword);

    @POST("api/auth/signup")
    Call<AuthResponse>signup(@Body SignupReq request);


    @POST("api/auth/login")
    Call<AuthResponse>login(@Body LoginReq request);

    @GET("api/users/{email}/topics")
    Call<UserTopicsResponse> getUserTopics(@Path("email") String email);

    @PUT("api/users/{email}/topics")
    Call<UserTopicsResponse> updateUserTopics(
            @Path("email") String email,
            @Body TopicsRequest request
    );
}
