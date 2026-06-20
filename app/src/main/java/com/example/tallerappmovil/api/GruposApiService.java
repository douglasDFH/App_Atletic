package com.example.tallerappmovil.api;

import com.example.tallerappmovil.model.GrupoDetalle;
import com.example.tallerappmovil.model.GrupoEntrenamiento;
import com.example.tallerappmovil.model.GrupoRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GruposApiService {

    @GET("grupos")
    Call<List<GrupoEntrenamiento>> listarGrupos();

    @GET("grupos/{id}")
    Call<GrupoDetalle> getGrupo(@Path("id") Long id);

    @POST("grupos")
    Call<GrupoEntrenamiento> crearGrupo(@Body GrupoRequest request);

    @PUT("grupos/{id}")
    Call<GrupoEntrenamiento> editarGrupo(@Path("id") Long id, @Body GrupoRequest request);

    @POST("grupos/{id}/atletas/{atletaId}")
    Call<Void> agregarAtleta(@Path("id") Long grupoId, @Path("atletaId") Long atletaId);

    @DELETE("grupos/{id}/atletas/{atletaId}")
    Call<Void> quitarAtleta(@Path("id") Long grupoId, @Path("atletaId") Long atletaId);
}
