package com.example.tallerappmovil.perfil;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.CambiarContrasenaRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CambiarContrasenaActivity extends AppCompatActivity {

    private TextInputLayout tilActual, tilNueva, tilConfirmar;
    private TextInputEditText etActual, etNueva, etConfirmar;
    private MaterialButton btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_contrasena);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tilActual    = findViewById(R.id.tilActual);
        tilNueva     = findViewById(R.id.tilNueva);
        tilConfirmar = findViewById(R.id.tilConfirmar);
        etActual     = findViewById(R.id.etActual);
        etNueva      = findViewById(R.id.etNueva);
        etConfirmar  = findViewById(R.id.etConfirmar);
        btnGuardar   = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(v -> intentarCambio());
    }

    private void intentarCambio() {
        String actual    = text(etActual);
        String nueva     = text(etNueva);
        String confirmar = text(etConfirmar);

        boolean valido = true;
        tilActual.setError(null);
        tilNueva.setError(null);
        tilConfirmar.setError(null);

        if (TextUtils.isEmpty(actual)) {
            tilActual.setError(getString(R.string.err_campo_requerido));
            valido = false;
        }
        if (TextUtils.isEmpty(nueva)) {
            tilNueva.setError(getString(R.string.err_campo_requerido));
            valido = false;
        } else if (nueva.length() < 8) {
            tilNueva.setError(getString(R.string.err_contrasena_corta));
            valido = false;
        }
        if (TextUtils.isEmpty(confirmar)) {
            tilConfirmar.setError(getString(R.string.err_campo_requerido));
            valido = false;
        } else if (!nueva.equals(confirmar)) {
            tilConfirmar.setError(getString(R.string.err_contrasenas_no_coinciden));
            valido = false;
        }
        if (!valido) return;

        btnGuardar.setEnabled(false);
        btnGuardar.setText(R.string.lbl_guardando);

        ApiClient.getUsuariosService()
                .cambiarContrasena(new CambiarContrasenaRequest(actual, nueva))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        btnGuardar.setEnabled(true);
                        btnGuardar.setText(R.string.btn_guardar_contrasena);
                        if (response.isSuccessful()) {
                            Toast.makeText(CambiarContrasenaActivity.this,
                                    getString(R.string.msg_contrasena_cambiada),
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else if (response.code() == 400 || response.code() == 401) {
                            tilActual.setError(getString(R.string.err_contrasena_actual_incorrecta));
                        } else {
                            Toast.makeText(CambiarContrasenaActivity.this,
                                    getString(R.string.err_conexion),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        btnGuardar.setEnabled(true);
                        btnGuardar.setText(R.string.btn_guardar_contrasena);
                        Toast.makeText(CambiarContrasenaActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String text(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
