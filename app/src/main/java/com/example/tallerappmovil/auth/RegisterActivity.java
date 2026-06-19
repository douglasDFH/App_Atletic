package com.example.tallerappmovil.auth;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.RegisterRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilNombre, tilCorreo, tilContrasena, tilConfirmar;
    private TextInputEditText etNombre, etCorreo, etContrasena, etConfirmar;
    private AutoCompleteTextView spinnerRol;
    private Button btnCrearCuenta;
    private ProgressBar progressBar;
    private TextView tvYaTengo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tilNombre    = findViewById(R.id.tilNombre);
        tilCorreo    = findViewById(R.id.tilCorreo);
        tilContrasena = findViewById(R.id.tilContrasena);
        tilConfirmar  = findViewById(R.id.tilConfirmar);
        etNombre     = findViewById(R.id.etNombre);
        etCorreo     = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        etConfirmar  = findViewById(R.id.etConfirmar);
        spinnerRol   = findViewById(R.id.spinnerRol);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        progressBar  = findViewById(R.id.progressBar);
        tvYaTengo    = findViewById(R.id.tvYaTengo);

        String[] roles = {getString(R.string.rol_atleta), getString(R.string.rol_padre)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, roles);
        spinnerRol.setAdapter(adapter);
        spinnerRol.setText(roles[0], false);

        btnCrearCuenta.setOnClickListener(v -> doRegister());
        tvYaTengo.setOnClickListener(v -> finish());
    }

    private void doRegister() {
        String nombre    = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
        String correo    = etCorreo.getText() != null ? etCorreo.getText().toString().trim() : "";
        String password  = etContrasena.getText() != null ? etContrasena.getText().toString() : "";
        String confirm   = etConfirmar.getText() != null ? etConfirmar.getText().toString() : "";
        String rolTexto  = spinnerRol.getText().toString();

        tilNombre.setError(null);
        tilCorreo.setError(null);
        tilContrasena.setError(null);
        tilConfirmar.setError(null);

        if (nombre.isEmpty()) { tilNombre.setError(getString(R.string.err_campo_requerido)); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) { tilCorreo.setError(getString(R.string.err_correo_invalido)); return; }
        if (password.length() < 8) { tilContrasena.setError(getString(R.string.err_contrasena_corta)); return; }
        if (!password.equals(confirm)) { tilConfirmar.setError(getString(R.string.err_contrasenas_no_coinciden)); return; }

        String rol = rolTexto.equals(getString(R.string.rol_padre)) ? "PADRE" : "ATLETA";

        setLoading(true);

        ApiClient.getAuthService()
                .register(new RegisterRequest(nombre, correo, password, rol))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        setLoading(false);
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this,
                                    getString(R.string.msg_registro_exitoso), Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "El correo ya está registrado", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(RegisterActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnCrearCuenta.setEnabled(!loading);
    }
}
