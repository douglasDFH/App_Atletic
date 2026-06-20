package com.example.tallerappmovil.api;

import com.example.tallerappmovil.model.CambiarContrasenaRequest;
import com.example.tallerappmovil.model.EditarPerfilRequest;
import com.example.tallerappmovil.model.PerfilUsuario;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface UsuariosApiService {

    @GET("usuarios/perfil")
    Call<PerfilUsuario> getPerfil();

    @PUT("usuarios/perfil")
    Call<PerfilUsuario> editarPerfil(@Body EditarPerfilRequest request);

    @PUT("usuarios/cambiar-contrasena")
    Call<Void> cambiarContrasena(@Body CambiarContrasenaRequest request);

    @PUT("usuarios/fcm-token")
    Call<Void> registrarFcmToken(@Body Map<String, String> body);

    @Multipart
    @PUT("usuarios/foto-perfil")
    Call<PerfilUsuario> subirFotoPerfil(@Part MultipartBody.Part foto);
}
