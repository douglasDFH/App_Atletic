package com.example.tallerappmovil.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificarCorreoActivity extends AppCompatActivity {

    private TextInputLayout tilCodigo;
    private TextInputEditText etCodigo;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar_correo);

        tilCodigo   = findViewById(R.id.tilCodigo);
        etCodigo    = findViewById(R.id.etCodigo);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.btnVerificar).setOnClickListener(v -> verificar());
        findViewById(R.id.tvVolver).setOnClickListener(v -> irALogin());

        // Si llegamos desde un deep link atletismo://verify?token=xxx
        Uri data = getIntent().getData();
        if (data != null && "verify".equals(data.getHost())) {
            String token = data.getQueryParameter("token");
            if (token != null && !token.isEmpty()) {
                etCodigo.setText(token);
                verificar();
            }
        }
    }

    private void verificar() {
        String codigo = etCodigo.getText() != null ? etCodigo.getText().toString().trim() : "";
        tilCodigo.setError(null);

        if (codigo.isEmpty()) {
            tilCodigo.setError(getString(R.string.err_codigo_requerido));
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.btnVerificar).setEnabled(false);

        ApiClient.getAuthService().verifyEmail(codigo)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressBar.setVisibility(View.GONE);
                        findViewById(R.id.btnVerificar).setEnabled(true);
                        if (response.isSuccessful()) {
                            Toast.makeText(VerificarCorreoActivity.this,
                                    getString(R.string.msg_correo_verificado), Toast.LENGTH_LONG).show();
                            irALogin();
                        } else {
                            tilCodigo.setError(getString(R.string.err_token_invalido));
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        findViewById(R.id.btnVerificar).setEnabled(true);
                        Toast.makeText(VerificarCorreoActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void irALogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
