package com.example.tallerappmovil.api;

import com.example.tallerappmovil.model.Competencia;
import com.example.tallerappmovil.model.CompetenciaRequest;
import com.example.tallerappmovil.model.AtletaInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CompetenciasApiService {

    @GET("competencias")
    Call<List<Competencia>> getCompetencias(@Query("estado") String estado);

    @GET("competencias/{id}")
    Call<Competencia> getCompetencia(@Path("id") Long id);

    @POST("competencias")
    Call<Competencia> crearCompetencia(@Body CompetenciaRequest request);

    @PUT("competencias/{id}")
    Call<Competencia> editarCompetencia(@Path("id") Long id, @Body CompetenciaRequest request);

    @DELETE("competencias/{id}")
    Call<Void> eliminarCompetencia(@Path("id") Long id);

    @POST("competencias/{id}/inscribir")
    Call<Void> inscribirse(@Path("id") Long id);

    @DELETE("competencias/{id}/desinscribir")
    Call<Void> desinscribirse(@Path("id") Long id);

    @GET("competencias/{id}/inscritos")
    Call<List<AtletaInfo>> getInscritos(@Path("id") Long id);
}
