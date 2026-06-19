package com.example.tallerappmovil.api;

import com.example.tallerappmovil.model.CambiarContrasenaRequest;
import com.example.tallerappmovil.model.EditarPerfilRequest;
import com.example.tallerappmovil.model.PerfilUsuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface UsuariosApiService {

    @GET("usuarios/perfil")
    Call<PerfilUsuario> getPerfil();

    @PUT("usuarios/perfil")
    Call<PerfilUsuario> editarPerfil(@Body EditarPerfilRequest request);

    @PUT("usuarios/cambiar-contrasena")
    Call<Void> cambiarContrasena(@Body CambiarContrasenaRequest request);
}
