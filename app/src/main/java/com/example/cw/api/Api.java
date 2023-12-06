package com.example.cw.api;

import com.example.cw.model.Event;
import com.example.cw.model.Job;
import com.example.cw.model.Link;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Body;

public interface Api {
    @FormUrlEncoded // because we are sending a from usl encoded
    @POST("user/signup/") // this is what the endpoint in the api is
    Call<ResponseBody> signUp(
            @Field("name") String username,
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

    @DELETE("event/{eventId}")
    Call<ResponseBody> deleteEvent(
            @Path("eventId") String eventId,
            @Header("Authorization") String authorizationHeader
    );

    @GET("event/user/{userId}")
    Call<List<Event>> getAdminEvents(
            @Path("userId") String userId,
            @Header("Authorization") String authorizationHeader
    );

    @Multipart
    @POST("event/")
    Call<Event> postEvent(
            @Header("Authorization") String authorizationHeader,
            @Part MultipartBody.Part image,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("location") RequestBody location,
            @Part("startDate") RequestBody startDate,
            @Part("finishDate") RequestBody finishDate,
            @Part("eventTimeStart") RequestBody eventTimeStart,
            @Part("eventTimeEnd") RequestBody eventTimeEnd
    );


    @GET("job/")
    Call<List<Job>> getJobs();

    @GET("job/user/{userId}")
    Call<List<Job>> getAdminJobs(@Path("userId") String userId);


    @DELETE("job/{jobId}")
    Call<ResponseBody> deleteJob(
            @Path("jobId") String jobId,
            @Header("Authorization") String authorizationHeader
    );
    @GET("link/")
    Call<List<Link>> getLinks();


}



