package com.example.paperpath;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Body;

import java.util.List;

public interface ApiService {

    @GET("api/conferences")
    Call<List<Conference>> getConferences();

    @GET("api/conferences/search")
    Call<List<Conference>> searchConferences(@Query("keyword") String keyword);

    @POST("api/auth/signup")
    Call<AuthResponse>signup(@Body SignupReq request);


    @POST("api/auth/login")
    Call<AuthResponse>login(@Body LoginReq request);


}
