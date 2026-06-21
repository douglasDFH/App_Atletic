package com.example.tallerappmovil.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREFS_NAME    = "atletismo_session";
    private static final String KEY_TOKEN     = "access_token";
    private static final String KEY_ROL       = "user_rol";
    private static final String KEY_NOMBRE    = "user_nombre";
    private static final String KEY_EMAIL     = "user_email";
    private static final String KEY_GRUPO_ID  = "user_grupo_id";
    private static final String KEY_GRUPO_NOM = "user_grupo_nombre";
    private static final String KEY_FOTO_URL  = "user_foto_url";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String token, String rol, String nombre) {
        prefs.edit()
             .putString(KEY_TOKEN, token)
             .putString(KEY_ROL, rol)
             .putString(KEY_NOMBRE, nombre)
             .apply();
    }

    public boolean isLoggedIn() {
        return prefs.contains(KEY_TOKEN);
    }

    public String getToken()    { return prefs.getString(KEY_TOKEN, null); }
    public String getUserRole() { return prefs.getString(KEY_ROL, ""); }
    public String getUserName() { return prefs.getString(KEY_NOMBRE, ""); }
    public String getUserEmail(){ return prefs.getString(KEY_EMAIL, ""); }

    public void saveUserName(String nombre) {
        prefs.edit().putString(KEY_NOMBRE, nombre).apply();
    }

    public void saveUserEmail(String email) {
        prefs.edit().putString(KEY_EMAIL, email).apply();
    }

    public Long getGrupoId() {
        long id = prefs.getLong(KEY_GRUPO_ID, -1L);
        return id == -1L ? null : id;
    }

    public String getGrupoNombre() {
        return prefs.getString(KEY_GRUPO_NOM, "");
    }

    public void saveGrupo(Long grupoId, String grupoNombre) {
        SharedPreferences.Editor ed = prefs.edit();
        if (grupoId != null) ed.putLong(KEY_GRUPO_ID, grupoId);
        else ed.remove(KEY_GRUPO_ID);
        ed.putString(KEY_GRUPO_NOM, grupoNombre != null ? grupoNombre : "");
        ed.apply();
    }

    public String getFotoUrl() {
        return prefs.getString(KEY_FOTO_URL, null);
    }

    public void saveFotoUrl(String url) {
        if (url != null && !url.isEmpty()) {
            prefs.edit().putString(KEY_FOTO_URL, url).apply();
        } else {
            prefs.edit().remove(KEY_FOTO_URL).apply();
        }
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
