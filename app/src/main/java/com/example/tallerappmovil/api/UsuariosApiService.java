package com.example.tallerappmovil.api;

import com.example.tallerappmovil.model.CambiarContrasenaRequest;
import com.example.tallerappmovil.model.EditarPerfilRequest;
import com.example.tallerappmovil.model.NotifPreferenciasRequest;
import com.example.tallerappmovil.model.PadreInfo;
import com.example.tallerappmovil.model.PerfilUsuario;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UsuariosApiService {

    @GET("usuarios/perfil")
    Call<PerfilUsuario> getPerfil();

    @PUT("usuarios/perfil")
    Call<PerfilUsuario> editarPerfil(@Body EditarPerfilRequest request);

    @PUT("usuarios/cambiar-contrasena")
    Call<Void> cambiarContrasena(@Body CambiarContrasenaRequest request);

    @PUT("usuarios/fcm-token")
    Call<Void> registrarFcmToken(@Body Map<String, String> body);

    @PUT("usuarios/notificaciones")
    Call<Void> actualizarNotifPreferencias(@Body NotifPreferenciasRequest req);

    @Multipart
    @PUT("usuarios/foto-perfil")
    Call<PerfilUsuario> subirFotoPerfil(@Part MultipartBody.Part foto);

    @Multipart
    @PUT("atletas/{id}/foto")
    Call<PerfilUsuario> subirFotoAtleta(@Path("id") Long atletaId, @Part MultipartBody.Part foto);

    // Vínculo padre/tutor ↔ hijo (solo entrenador/admin)
    @GET("padres")
    Call<List<PadreInfo>> getPadres();

    @PUT("padres/{padreId}/hijo/{atletaId}")
    Call<Void> vincularHijo(@Path("padreId") Long padreId, @Path("atletaId") Long atletaId);

    @DELETE("padres/{padreId}/hijo")
    Call<Void> desvincularHijo(@Path("padreId") Long padreId);
}
