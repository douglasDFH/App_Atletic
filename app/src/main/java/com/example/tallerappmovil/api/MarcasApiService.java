package com.example.tallerappmovil.api;

import com.example.tallerappmovil.model.AtletaInfo;
import com.example.tallerappmovil.model.MarcaPersonal;
import com.example.tallerappmovil.model.MarcaRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MarcasApiService {

    @GET("marcas")
    Call<List<MarcaPersonal>> getMarcas(@Query("disciplina") String disciplina);

    @GET("marcas")
    Call<List<MarcaPersonal>> getMarcasPorAtleta(
            @Query("atletaId") Long atletaId,
            @Query("disciplina") String disciplina);

    @POST("marcas")
    Call<MarcaPersonal> registrarMarca(@Body MarcaRequest request);

    @DELETE("marcas/{id}")
    Call<Void> eliminarMarca(@Path("id") Long id);

    // Ranking / leaderboard: todas las marcas (por disciplina si se indica)
    @GET("marcas/ranking")
    Call<List<MarcaPersonal>> getRanking(@Query("disciplina") String disciplina);

    @GET("atletas")
    Call<List<AtletaInfo>> getAtletas();
}
