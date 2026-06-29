package com.example.tallerappmovil.api;

import android.content.Intent;

import com.example.tallerappmovil.AtletismoApp;
import com.example.tallerappmovil.auth.LoginActivity;
import com.example.tallerappmovil.session.SessionManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://xk30jfxsb0mt15cbvkxy0jsn.72.60.143.106.sslip.io/api/v1/";
    private static final String SERVER_URL = "http://xk30jfxsb0mt15cbvkxy0jsn.72.60.143.106.sslip.io";

    public static String resolveUrl(String path) {
        if (path == null || path.isEmpty()) return null;
        if (path.startsWith("http")) return path;
        return SERVER_URL + path;
    }

    private static Retrofit retrofit;
    private static String authToken = null;

    // Evita relanzar LoginActivity varias veces si llegan 401 simultáneos.
    // Se re-arma al iniciar una nueva sesión (setToken).
    private static final java.util.concurrent.atomic.AtomicBoolean handlingUnauthorized =
            new java.util.concurrent.atomic.AtomicBoolean(false);

    public static void setToken(String token) {
        authToken = token;
        handlingUnauthorized.set(false);
    }

    public static void clearToken() {
        authToken = null;
        retrofit = null;
    }

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder();
                        if (authToken != null) {
                            builder.addHeader("Authorization", "Bearer " + authToken);
                        }
                        Response response = chain.proceed(builder.build());
                        // Un 401 en /auth/** (login, registro, recuperación) significa
                        // "credenciales inválidas" y debe mostrarse en pantalla, no relanzar Login.
                        boolean esEndpointAuth = original.url().encodedPath().contains("/auth/");
                        if (response.code() == 401 && !esEndpointAuth) {
                            handleUnauthorized();
                        }
                        return response;
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static void handleUnauthorized() {
        // Solo el primer 401 dispara la redirección; el flag se re-arma en setToken().
        if (!handlingUnauthorized.compareAndSet(false, true)) return;
        android.os.Handler main = new android.os.Handler(android.os.Looper.getMainLooper());
        main.post(() -> {
            android.content.Context ctx = AtletismoApp.getContext();
            new SessionManager(ctx).clearSession();
            authToken = null;
            retrofit   = null;
            android.widget.Toast.makeText(ctx,
                    "Tu sesión expiró, inicia sesión de nuevo",
                    android.widget.Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ctx, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ctx.startActivity(intent);
        });
    }

    public static AuthApiService getAuthService() {
        return getRetrofit().create(AuthApiService.class);
    }

    public static AgendaApiService getAgendaService() {
        return getRetrofit().create(AgendaApiService.class);
    }

    public static AsistenciaApiService getAsistenciaService() {
        return getRetrofit().create(AsistenciaApiService.class);
    }

    public static MarcasApiService getMarcasService() {
        return getRetrofit().create(MarcasApiService.class);
    }

    public static CompetenciasApiService getCompetenciasService() {
        return getRetrofit().create(CompetenciasApiService.class);
    }

    public static AtletasApiService getAtletasService() {
        return getRetrofit().create(AtletasApiService.class);
    }

    public static GruposApiService getGruposService() {
        return getRetrofit().create(GruposApiService.class);
    }

    public static NotificacionesApiService getNotificacionesService() {
        return getRetrofit().create(NotificacionesApiService.class);
    }

    public static UsuariosApiService getUsuariosService() {
        return getRetrofit().create(UsuariosApiService.class);
    }

    public static DisciplinasApiService getDisciplinasService() {
        return getRetrofit().create(DisciplinasApiService.class);
    }

    public static DatosFisicosApiService getDatosFisicosService() {
        return getRetrofit().create(DatosFisicosApiService.class);
    }
}
