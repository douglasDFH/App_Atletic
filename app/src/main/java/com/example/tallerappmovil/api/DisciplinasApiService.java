package com.example.tallerappmovil.api;

import com.example.tallerappmovil.model.Disciplina;
import com.example.tallerappmovil.model.DisciplinaRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface DisciplinasApiService {

    /** Disciplinas activas — usado por todos los spinners de la app */
    @GET("disciplinas")
    Call<List<Disciplina>> listar();

    /** Todas (activas + inactivas) — solo para gestión del entrenador */
    @GET("disciplinas/todas")
    Call<List<Disciplina>> listarTodas();

    @GET("disciplinas/{id}")
    Call<Disciplina> obtener(@Path("id") Long id);

    @POST("disciplinas")
    Call<Disciplina> crear(@Body DisciplinaRequest req);

    @PUT("disciplinas/{id}")
    Call<Disciplina> actualizar(@Path("id") Long id, @Body DisciplinaRequest req);

    @PUT("disciplinas/{id}/estado")
    Call<Void> cambiarEstado(@Path("id") Long id, @Body Map<String, Boolean> body);
}
