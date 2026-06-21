package com.example.tallerappmovil.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    public static final String EXTRA_TOKEN = "token";

    private TextInputLayout tilToken, tilNueva, tilConfirmar;
    private TextInputEditText etToken, etNueva, etConfirmar;
    private Button btnReset;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        tilToken      = findViewById(R.id.tilToken);
        tilNueva      = findViewById(R.id.tilNuevaContrasena);
        tilConfirmar  = findViewById(R.id.tilConfirmarContrasena);
        etToken       = findViewById(R.id.etToken);
        etNueva       = findViewById(R.id.etNuevaContrasena);
        etConfirmar   = findViewById(R.id.etConfirmarContrasena);
        btnReset      = findViewById(R.id.btnReset);
        progressBar   = findViewById(R.id.progressBar);

        // Token desde deep link atletismo://reset?token=...
        Uri data = getIntent().getData();
        if (data != null && "atletismo".equals(data.getScheme())) {
            String token = data.getQueryParameter("token");
            if (token != null) etToken.setText(token.trim());
        }

        // Token desde Intent extra (navegación interna)
        String tokenExtra = getIntent().getStringExtra(EXTRA_TOKEN);
        if (tokenExtra != null && !tokenExtra.isEmpty()) {
            etToken.setText(tokenExtra.trim());
        }

        btnReset.setOnClickListener(v -> doReset());
        findViewById(R.id.tvVolver).setOnClickListener(v -> finish());
    }

    private void doReset() {
        tilToken.setError(null);
        tilNueva.setError(null);
        tilConfirmar.setError(null);

        String token    = etToken.getText() != null ? etToken.getText().toString().trim() : "";
        String nueva    = etNueva.getText() != null ? etNueva.getText().toString() : "";
        String confirmar = etConfirmar.getText() != null ? etConfirmar.getText().toString() : "";

        boolean ok = true;

        if (token.isEmpty()) {
            tilToken.setError(getString(R.string.err_campo_requerido));
            ok = false;
        }
        if (nueva.length() < 8) {
            tilNueva.setError(getString(R.string.err_contrasena_corta));
            ok = false;
        }
        if (!nueva.equals(confirmar)) {
            tilConfirmar.setError(getString(R.string.err_contrasenas_no_coinciden));
            ok = false;
        }
        if (!ok) return;

        setLoading(true);

        Map<String, String> body = new HashMap<>();
        body.put("token", token);
        body.put("nuevaContrasena", nueva);

        ApiClient.getAuthService()
                .resetPassword(body)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (isFinishing()) return;
                        setLoading(false);
                        if (response.isSuccessful()) {
                            Toast.makeText(ResetPasswordActivity.this,
                                    getString(R.string.msg_contrasena_restablecida), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            String msg = response.code() == 400
                                    ? getString(R.string.err_token_invalido)
                                    : getString(R.string.err_conexion);
                            Toast.makeText(ResetPasswordActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        if (isFinishing()) return;
                        setLoading(false);
                        Toast.makeText(ResetPasswordActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnReset.setEnabled(!loading);
    }
}
