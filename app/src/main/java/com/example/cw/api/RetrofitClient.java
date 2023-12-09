package com.example.cw.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://mdp-server-07db49d63c9e.herokuapp.com/api/";
//    private static final String BASE_URL = " https://27ef-113-23-202-18.ngrok-free.app";

    private  static  RetrofitClient mInstance;
    private Retrofit retrofit;


    public RetrofitClient(){
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static  synchronized RetrofitClient getInstance(){
        if (mInstance == null){
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }
    public Api getApi(){
        return retrofit.create(Api.class);
    }
}
