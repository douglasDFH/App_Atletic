package com.example.tallerappmovil.api;

import com.example.tallerappmovil.model.AtletaCrearRequest;
import com.example.tallerappmovil.model.AtletaDetalle;
import com.example.tallerappmovil.model.AtletaEditarRequest;
import com.example.tallerappmovil.model.AtletaInfo;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AtletasApiService {

    @GET("atletas")
    Call<List<AtletaInfo>> getAtletas();

    @GET("atletas/{id}")
    Call<AtletaDetalle> getAtleta(@Path("id") Long id);

    // Gestión por el entrenador (RF-04, HU-12)
    @POST("atletas")
    Call<Void> crearAtleta(@Body AtletaCrearRequest req);

    @PUT("atletas/{id}")
    Call<AtletaDetalle> editarAtleta(@Path("id") Long id, @Body AtletaEditarRequest req);

    @PUT("atletas/{id}/estado")
    Call<Void> cambiarEstado(@Path("id") Long id, @Body Map<String, Boolean> body);
}
