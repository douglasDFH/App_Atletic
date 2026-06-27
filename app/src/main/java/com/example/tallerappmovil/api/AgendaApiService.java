package com.example.tallerappmovil.api;

import com.example.tallerappmovil.model.GrupoEntrenamiento;
import com.example.tallerappmovil.model.SesionCreateRequest;
import com.example.tallerappmovil.model.SesionEntrenamiento;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AgendaApiService {

    @GET("sesiones")
    Call<List<SesionEntrenamiento>> listarPorSemana(@Query("semana") String semana);

    @GET("sesiones")
    Call<List<SesionEntrenamiento>> listarPorGrupo(@Query("semana") String semana,
                                                   @Query("grupoId") Long grupoId);

    @POST("sesiones")
    Call<SesionEntrenamiento> crear(@Body SesionCreateRequest request);

    @PUT("sesiones/{id}")
    Call<SesionEntrenamiento> editar(@Path("id") Long id, @Body SesionCreateRequest request);

    @PUT("sesiones/{id}/cancelar")
    Call<SesionEntrenamiento> cancelar(@Path("id") Long id, @Body Map<String, String> body);

    @DELETE("sesiones/{id}")
    Call<Void> eliminar(@Path("id") Long id);

    @GET("grupos-agenda")
    Call<List<GrupoEntrenamiento>> listarGrupos();
}
