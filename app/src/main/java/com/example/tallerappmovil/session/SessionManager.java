package com.example.tallerappmovil.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREFS_NAME = "atletismo_session";
    private static final String KEY_TOKEN   = "access_token";
    private static final String KEY_ROL     = "user_rol";
    private static final String KEY_NOMBRE  = "user_nombre";

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

    public String getToken()   { return prefs.getString(KEY_TOKEN, null); }
    public String getUserRole() { return prefs.getString(KEY_ROL, ""); }
    public String getUserName() { return prefs.getString(KEY_NOMBRE, ""); }

    public void saveUserName(String nombre) {
        prefs.edit().putString(KEY_NOMBRE, nombre).apply();
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
