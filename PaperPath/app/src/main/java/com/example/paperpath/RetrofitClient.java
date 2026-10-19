package com.example.paperpath;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

//private static final String BASE_URL = "http://10.0.2.2:8080/";       // use this ip when using android emulator
private static final String BASE_URL = "http://10.0.2.2:8080/";       // replace the ip with your laptop's ip address

private static Retrofit retrofit;

public static ApiService getApiService(){
    if(retrofit == null) {
        retrofit = new
                Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(
                        GsonConverterFactory.create()
                )
                .build();
    }
    return retrofit.create(ApiService.class);
}
}
