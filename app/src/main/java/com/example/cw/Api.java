package com.example.cw;

import com.example.cw.model.Event;
import com.example.cw.model.Job;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

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

    @FormUrlEncoded
    @POST("user/admin/login")
    Call<ResponseBody> adminLogin(
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("event/")
    Call<List<Event>> getEvents();

    @GET("event/user/{userId}")
    Call<List<Event>> getAdminEvents(@Path("userId") String userId);

    @GET("job/")
    Call<List<Job>> getJobs();
}



