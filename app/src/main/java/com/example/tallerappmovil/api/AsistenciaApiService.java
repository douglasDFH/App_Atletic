package com.example.tallerappmovil.api;

import com.example.tallerappmovil.model.AsistenciaAtleta;
import com.example.tallerappmovil.model.AsistenciaHistorial;
import com.example.tallerappmovil.model.AsistenciaRequest;
import com.example.tallerappmovil.model.ReporteAtleta;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AsistenciaApiService {

    @GET("sesiones/{id}/asistencia")
    Call<List<AsistenciaAtleta>> getAsistencia(@Path("id") Long sesionId);

    @POST("sesiones/{id}/asistencia")
    Call<Void> guardarAsistencia(@Path("id") Long sesionId,
                                 @Body List<AsistenciaRequest> lista);

    @GET("asistencia/mi-historial")
    Call<List<AsistenciaHistorial>> getMiHistorial();

    @GET("atletas/{id}/asistencia")
    Call<List<AsistenciaHistorial>> getHistorialAtleta(@Path("id") Long atletaId);

    @GET("asistencia/reporte")
    Call<List<ReporteAtleta>> getReporte(@Query("grupoId") Long grupoId,
                                         @Query("mes") String mes);
}
