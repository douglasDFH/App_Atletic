package com.example.tallerappmovil.auth;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificarCorreoActivity extends AppCompatActivity {

    private TextInputLayout tilCodigo;
    private TextInputEditText etCodigo;
    private ProgressBar progressBar;
    private String correoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar_correo);

        tilCodigo    = findViewById(R.id.tilCodigo);
        etCodigo     = findViewById(R.id.etCodigo);
        progressBar  = findViewById(R.id.progressBar);
        correoUsuario = getIntent().getStringExtra("correo");

        findViewById(R.id.btnVerificar).setOnClickListener(v -> verificar());
        findViewById(R.id.btnPegar).setOnClickListener(v -> pegarDesdePortapapeles());
        findViewById(R.id.tvReenviar).setOnClickListener(v -> reenviarCorreo());
        findViewById(R.id.tvVolver).setOnClickListener(v -> irALogin());

        // Si llegamos desde deep link atletismo://verify?token=xxx
        Uri data = getIntent().getData();
        if (data != null && "verify".equals(data.getHost())) {
            String token = data.getQueryParameter("token");
            if (token != null && !token.isEmpty()) {
                etCodigo.setText(token);
                verificar();
            }
        }
    }

    private void pegarDesdePortapapeles() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null && clipboard.hasPrimaryClip()) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            String texto = item.getText() != null ? item.getText().toString().trim() : "";
            if (!texto.isEmpty()) {
                etCodigo.setText(texto);
                tilCodigo.setError(null);
            } else {
                Toast.makeText(this, "El portapapeles está vacío", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No hay texto copiado en el portapapeles", Toast.LENGTH_SHORT).show();
        }
    }

    private void reenviarCorreo() {
        if (correoUsuario == null || correoUsuario.isEmpty()) {
            Toast.makeText(this, "No se pudo identificar el correo. Vuelve a registrarte.", Toast.LENGTH_LONG).show();
            return;
        }

        setLoading(true);
        Map<String, String> body = new HashMap<>();
        body.put("correo", correoUsuario);

        ApiClient.getAuthService().resendVerification(body)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        setLoading(false);
                        Toast.makeText(VerificarCorreoActivity.this,
                                "Correo reenviado. Revisa tu bandeja (y la carpeta spam).",
                                Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(VerificarCorreoActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verificar() {
        String codigo = etCodigo.getText() != null ? etCodigo.getText().toString().trim() : "";
        tilCodigo.setError(null);

        if (codigo.isEmpty()) {
            tilCodigo.setError(getString(R.string.err_codigo_requerido));
            return;
        }

        setLoading(true);

        ApiClient.getAuthService().verifyEmail(codigo)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        setLoading(false);
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
                        setLoading(false);
                        Toast.makeText(VerificarCorreoActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        findViewById(R.id.btnVerificar).setEnabled(!loading);
        findViewById(R.id.btnPegar).setEnabled(!loading);
        findViewById(R.id.tvReenviar).setEnabled(!loading);
    }

    private void irALogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
