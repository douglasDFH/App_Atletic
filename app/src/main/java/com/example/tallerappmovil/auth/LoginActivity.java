package com.example.tallerappmovil.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tallerappmovil.BuildConfig;
import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.dashboard.AtletaDashboardActivity;
import com.example.tallerappmovil.dashboard.DashboardActivity;
import com.example.tallerappmovil.model.LoginRequest;
import com.example.tallerappmovil.model.TokenResponse;
import com.example.tallerappmovil.session.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilCorreo, tilContrasena;
    private TextInputEditText etCorreo, etContrasena;
    private CheckBox cbRecordarme;
    private Button btnIngresar;
    private ProgressBar progressBar;
    private TextView tvOlvideContrasena, tvRegistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);
        if (session.isLoggedIn()) {
            ApiClient.setToken(session.getToken());
            redirectToDashboard(session.getUserRole(), session.getUserName());
            return;
        }

        setContentView(R.layout.activity_login);

        tilCorreo        = findViewById(R.id.tilCorreo);
        tilContrasena    = findViewById(R.id.tilContrasena);
        etCorreo         = findViewById(R.id.etCorreo);
        etContrasena     = findViewById(R.id.etContrasena);
        cbRecordarme     = findViewById(R.id.cbRecordarme);
        btnIngresar      = findViewById(R.id.btnIngresar);
        progressBar      = findViewById(R.id.progressBar);
        tvOlvideContrasena = findViewById(R.id.tvOlvideContrasena);
        tvRegistrarse    = findViewById(R.id.tvRegistrarse);

        TextView tvVersion = findViewById(R.id.tvVersion);
        tvVersion.setText("v" + BuildConfig.VERSION_NAME);

        btnIngresar.setOnClickListener(v -> doLogin());
        tvOlvideContrasena.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
        tvRegistrarse.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void doLogin() {
        String correo     = etCorreo.getText() != null ? etCorreo.getText().toString().trim() : "";
        String contrasena = etContrasena.getText() != null ? etContrasena.getText().toString() : "";

        tilCorreo.setError(null);
        tilContrasena.setError(null);

        if (correo.isEmpty()) {
            tilCorreo.setError(getString(R.string.err_campo_requerido));
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilCorreo.setError(getString(R.string.err_correo_invalido));
            return;
        }
        if (contrasena.isEmpty()) {
            tilContrasena.setError(getString(R.string.err_campo_requerido));
            return;
        }

        setLoading(true);

        boolean recordarme = cbRecordarme != null && cbRecordarme.isChecked();
        ApiClient.getAuthService()
                .login(new LoginRequest(correo, contrasena, recordarme))
                .enqueue(new Callback<TokenResponse>() {
                    @Override
                    public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            TokenResponse token = response.body();
                            new SessionManager(LoginActivity.this)
                                    .saveSession(token.getAccessToken(), token.getRol(), token.getNombreCompleto());
                            ApiClient.setToken(token.getAccessToken());
                            redirectToDashboard(token.getRol(), token.getNombreCompleto());
                        } else if (response.code() == 423) {
                            // Cuenta bloqueada por intentos fallidos (HU-02)
                            String msg = extractErrorMessage(response);
                            Toast.makeText(LoginActivity.this,
                                    msg != null ? msg : "Cuenta bloqueada temporalmente. Intenta más tarde.",
                                    Toast.LENGTH_LONG).show();
                        } else if (response.code() == 403) {
                            // Correo sin verificar (HU-01)
                            Toast.makeText(LoginActivity.this,
                                    getString(R.string.err_correo_no_verificado), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LoginActivity.this, VerificarCorreoActivity.class));
                        } else {
                            tilCorreo.setError(getString(R.string.err_credenciales));
                            tilContrasena.setError(getString(R.string.err_credenciales));
                            Toast.makeText(LoginActivity.this,
                                    getString(R.string.err_credenciales), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TokenResponse> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(LoginActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void redirectToDashboard(String rol, String nombre) {
        enviarFcmTokenAlServidor();
        boolean esEntrenador = "ENTRENADOR".equals(rol) || "ADMIN".equals(rol);
        Class<?> destino = esEntrenador ? DashboardActivity.class : AtletaDashboardActivity.class;
        Intent intent = new Intent(this, destino);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void enviarFcmTokenAlServidor() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            Map<String, String> body = new HashMap<>();
            body.put("token", token);
            ApiClient.getUsuariosService().registrarFcmToken(body)
                    .enqueue(new Callback<Void>() {
                        @Override public void onResponse(Call<Void> c, Response<Void> r) {}
                        @Override public void onFailure(Call<Void> c, Throwable t) {}
                    });
        });
    }

    private String extractErrorMessage(retrofit2.Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String body = response.errorBody().string();
                int idx = body.indexOf("\"message\":");
                if (idx >= 0) {
                    int start = body.indexOf('"', idx + 10) + 1;
                    int end   = body.indexOf('"', start);
                    if (start > 0 && end > start) return body.substring(start, end);
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnIngresar.setEnabled(!loading);
    }
}
