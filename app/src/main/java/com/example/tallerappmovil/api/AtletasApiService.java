package com.example.tallerappmovil.api;

import com.example.tallerappmovil.model.AtletaDetalle;
import com.example.tallerappmovil.model.AtletaInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AtletasApiService {

    @GET("atletas")
    Call<List<AtletaInfo>> getAtletas();

    @GET("atletas/{id}")
    Call<AtletaDetalle> getAtleta(@Path("id") Long id);
}
