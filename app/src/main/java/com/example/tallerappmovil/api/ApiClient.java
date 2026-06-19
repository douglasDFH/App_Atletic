package com.example.tallerappmovil.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // 10.0.2.2 = localhost desde el emulador Android
    private static final String BASE_URL = "http://10.0.2.2:8080/api/v1/";

    private static Retrofit retrofit;
    private static String authToken = null;

    public static void setToken(String token) {
        authToken = token;
    }

    public static void clearToken() {
        authToken = null;
    }

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(chain -> {
                        Request.Builder builder = chain.request().newBuilder();
                        if (authToken != null) {
                            builder.addHeader("Authorization", "Bearer " + authToken);
                        }
                        return chain.proceed(builder.build());
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
}
