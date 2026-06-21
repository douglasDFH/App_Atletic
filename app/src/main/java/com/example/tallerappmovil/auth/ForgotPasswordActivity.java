package com.example.tallerappmovil.auth;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout tilCorreo;
    private TextInputEditText etCorreo;
    private Button btnEnviar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        tilCorreo   = findViewById(R.id.tilCorreo);
        etCorreo    = findViewById(R.id.etCorreo);
        btnEnviar   = findViewById(R.id.btnEnviar);
        progressBar = findViewById(R.id.progressBar);

        btnEnviar.setOnClickListener(v -> doForgotPassword());
        findViewById(R.id.tvVolver).setOnClickListener(v -> finish());
    }

    private void doForgotPassword() {
        String correo = etCorreo.getText() != null
                ? etCorreo.getText().toString().trim()
                : "";

        tilCorreo.setError(null);

        if (correo.isEmpty()) {
            tilCorreo.setError(getString(R.string.err_campo_requerido));
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilCorreo.setError(getString(R.string.err_correo_invalido));
            return;
        }

        setLoading(true);

        Map<String, String> body = new HashMap<>();
        body.put("correo", correo);

        ApiClient.getAuthService()
                .forgotPassword(body)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (isFinishing()) return;
                        setLoading(false);
                        Toast.makeText(ForgotPasswordActivity.this,
                                getString(R.string.msg_correo_enviado), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class));
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        if (isFinishing()) return;
                        setLoading(false);
                        Toast.makeText(ForgotPasswordActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnEnviar.setEnabled(!loading);
    }
}
