package com.example.tallerappmovil.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.dashboard.DashboardActivity;
import com.example.tallerappmovil.model.LoginRequest;
import com.example.tallerappmovil.model.TokenResponse;
import com.example.tallerappmovil.session.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilCorreo, tilContrasena;
    private TextInputEditText etCorreo, etContrasena;
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
        btnIngresar      = findViewById(R.id.btnIngresar);
        progressBar      = findViewById(R.id.progressBar);
        tvOlvideContrasena = findViewById(R.id.tvOlvideContrasena);
        tvRegistrarse    = findViewById(R.id.tvRegistrarse);

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

        ApiClient.getAuthService()
                .login(new LoginRequest(correo, contrasena))
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
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnIngresar.setEnabled(!loading);
    }
}
