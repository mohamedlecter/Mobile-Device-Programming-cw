package com.example.cw;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {
    @FormUrlEncoded // because we are sending a from usl encoded
    @POST("user/signup/") // this is what the endpoint in the api is
    Call<ResponseBody> signUp(
            @Field("username") String username,
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded // because we are sending a from usl encoded
    @POST("user/login/") // this is what the endpoint in the api is
    Call<ResponseBody> login(
            @Field("email") String email,
            @Field("password") String password
    );
}
