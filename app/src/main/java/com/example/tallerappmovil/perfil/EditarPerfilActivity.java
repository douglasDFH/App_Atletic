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
import com.example.tallerappmovil.model.EditarPerfilRequest;
import com.example.tallerappmovil.model.PerfilUsuario;
import com.example.tallerappmovil.session.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarPerfilActivity extends AppCompatActivity {

    private TextInputLayout tilNombre, tilEmail;
    private TextInputEditText etNombre, etEmail;
    private MaterialButton btnGuardar;
    private View progressBar;
    private android.widget.TextView tvAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tilNombre   = findViewById(R.id.tilNombre);
        tilEmail    = findViewById(R.id.tilEmail);
        etNombre    = findViewById(R.id.etNombre);
        etEmail     = findViewById(R.id.etEmail);
        btnGuardar  = findViewById(R.id.btnGuardar);
        progressBar = findViewById(R.id.progressBar);
        tvAvatar    = findViewById(R.id.tvAvatar);

        btnGuardar.setEnabled(false);
        cargarPerfil();

        btnGuardar.setOnClickListener(v -> guardarCambios());
    }

    private void cargarPerfil() {
        progressBar.setVisibility(View.VISIBLE);

        ApiClient.getUsuariosService().getPerfil()
                .enqueue(new Callback<PerfilUsuario>() {
                    @Override
                    public void onResponse(Call<PerfilUsuario> call, Response<PerfilUsuario> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            PerfilUsuario p = response.body();
                            etNombre.setText(p.getNombreCompleto());
                            etEmail.setText(p.getEmail());
                            actualizarAvatar(p.getNombreCompleto());
                        } else {
                            // Fallback desde SessionManager
                            SessionManager session = new SessionManager(EditarPerfilActivity.this);
                            etNombre.setText(session.getUserName());
                            actualizarAvatar(session.getUserName());
                        }
                        btnGuardar.setEnabled(true);
                    }

                    @Override
                    public void onFailure(Call<PerfilUsuario> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        SessionManager session = new SessionManager(EditarPerfilActivity.this);
                        etNombre.setText(session.getUserName());
                        actualizarAvatar(session.getUserName());
                        btnGuardar.setEnabled(true);
                    }
                });
    }

    private void guardarCambios() {
        String nombre = text(etNombre);
        String email  = text(etEmail);

        boolean valido = true;
        tilNombre.setError(null);
        tilEmail.setError(null);

        if (TextUtils.isEmpty(nombre)) {
            tilNombre.setError(getString(R.string.err_campo_requerido));
            valido = false;
        }
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.err_correo_invalido));
            valido = false;
        }
        if (!valido) return;

        btnGuardar.setEnabled(false);
        btnGuardar.setText(R.string.lbl_guardando);

        ApiClient.getUsuariosService()
                .editarPerfil(new EditarPerfilRequest(nombre, email))
                .enqueue(new Callback<PerfilUsuario>() {
                    @Override
                    public void onResponse(Call<PerfilUsuario> call, Response<PerfilUsuario> response) {
                        btnGuardar.setEnabled(true);
                        btnGuardar.setText(R.string.btn_guardar_perfil);
                        if (response.isSuccessful() && response.body() != null) {
                            // Actualizar nombre en sesión local
                            new SessionManager(EditarPerfilActivity.this)
                                    .saveUserName(response.body().getNombreCompleto());
                            Toast.makeText(EditarPerfilActivity.this,
                                    getString(R.string.msg_perfil_actualizado),
                                    Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(EditarPerfilActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PerfilUsuario> call, Throwable t) {
                        btnGuardar.setEnabled(true);
                        btnGuardar.setText(R.string.btn_guardar_perfil);
                        Toast.makeText(EditarPerfilActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void actualizarAvatar(String nombre) {
        if (nombre != null && !nombre.isEmpty()) {
            tvAvatar.setText(String.valueOf(nombre.charAt(0)).toUpperCase());
        }
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
