package com.example.tallerappmovil.api;

import com.example.tallerappmovil.model.DatosFisicos;
import com.example.tallerappmovil.model.DatosFisicosRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DatosFisicosApiService {

    @POST("datos-fisicos")
    Call<DatosFisicos> registrar(@Body DatosFisicosRequest req);

    @GET("datos-fisicos/{atletaId}")
    Call<List<DatosFisicos>> historial(@Path("atletaId") Long atletaId);

    @GET("datos-fisicos/{atletaId}/ultimo")
    Call<DatosFisicos> ultimo(@Path("atletaId") Long atletaId);
}
