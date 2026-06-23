package com.example.tallerappmovil.api;

import com.example.tallerappmovil.model.LoginRequest;
import com.example.tallerappmovil.model.RegisterRequest;
import com.example.tallerappmovil.model.TokenResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApiService {

    @POST("auth/login")
    Call<TokenResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<Void> register(@Body RegisterRequest request);

    @POST("auth/forgot-password")
    Call<Void> forgotPassword(@Body Map<String, String> body);

    @POST("auth/reset-password")
    Call<Void> resetPassword(@Body Map<String, String> body);

    @GET("auth/verify-email")
    Call<Void> verifyEmail(@Query("token") String token);
}
