package com.example.tallerappmovil.perfil;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.EditarPerfilRequest;
import com.example.tallerappmovil.model.PerfilUsuario;
import com.example.tallerappmovil.session.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarPerfilActivity extends AppCompatActivity {

    private TextInputLayout tilNombre, tilEmail;
    private TextInputEditText etNombre, etEmail;
    private MaterialButton btnGuardar;
    private View progressBar;
    private android.widget.TextView tvAvatar;
    private ImageView ivAvatar;

    private ActivityResultLauncher<String> pickImageLauncher;

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
        ivAvatar    = findViewById(R.id.ivAvatar);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> { if (uri != null) subirFoto(uri); });

        findViewById(R.id.frameAvatar).setOnClickListener(v ->
                pickImageLauncher.launch("image/*"));

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
                            cargarFoto(p.getFotoUrl());
                        } else {
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

    private void cargarFoto(String url) {
        if (url == null || url.isEmpty()) return;
        ivAvatar.setVisibility(View.VISIBLE);
        tvAvatar.setVisibility(View.GONE);
        Glide.with(this).load(url).transform(new CircleCrop()).into(ivAvatar);
    }

    private void subirFoto(Uri uri) {
        try {
            ContentResolver cr = getContentResolver();
            String mime = cr.getType(uri);
            if (mime == null) mime = "image/jpeg";
            InputStream is = cr.openInputStream(uri);
            if (is == null) return;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int len;
            while ((len = is.read(buf)) != -1) baos.write(buf, 0, len);
            is.close();
            byte[] bytes = baos.toByteArray();

            RequestBody rb  = RequestBody.create(MediaType.parse(mime), bytes);
            MultipartBody.Part part = MultipartBody.Part.createFormData("foto", "foto.jpg", rb);

            progressBar.setVisibility(View.VISIBLE);
            ApiClient.getUsuariosService().subirFotoPerfil(part)
                    .enqueue(new Callback<PerfilUsuario>() {
                        @Override
                        public void onResponse(Call<PerfilUsuario> call, Response<PerfilUsuario> r) {
                            progressBar.setVisibility(View.GONE);
                            if (r.isSuccessful() && r.body() != null) {
                                cargarFoto(r.body().getFotoUrl());
                                Toast.makeText(EditarPerfilActivity.this,
                                        getString(R.string.msg_foto_actualizada), Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                            } else {
                                Toast.makeText(EditarPerfilActivity.this,
                                        getString(R.string.err_foto_subida), Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<PerfilUsuario> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(EditarPerfilActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.err_foto_subida), Toast.LENGTH_SHORT).show();
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
