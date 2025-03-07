package com.example.perpusdig.api;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient  {
    private static final String BASE_URL = "https://perpusdig.pbltifnganjuk.com/api/";
    private static Retrofit retrofit;
    public static Retrofit getRetrofitInstance(){
        if(retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl( BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
