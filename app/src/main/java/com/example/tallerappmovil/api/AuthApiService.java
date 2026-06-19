package com.example.tallerappmovil.api;

import com.example.tallerappmovil.model.LoginRequest;
import com.example.tallerappmovil.model.RegisterRequest;
import com.example.tallerappmovil.model.TokenResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("auth/login")
    Call<TokenResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<Void> register(@Body RegisterRequest request);

    @POST("auth/forgot-password")
    Call<Void> forgotPassword(@Body Map<String, String> body);
}
