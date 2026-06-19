package com.example.tallerappmovil.api;

import com.example.tallerappmovil.model.Notificacion;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NotificacionesApiService {

    @GET("notificaciones")
    Call<List<Notificacion>> getNotificaciones();

    @PUT("notificaciones/{id}/leer")
    Call<Void> marcarLeida(@Path("id") Long id);

    @PUT("notificaciones/leer-todas")
    Call<Void> marcarTodasLeidas();
}
