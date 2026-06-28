package com.example.tallerappmovil.auth;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.RegisterRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilNombre, tilCorreo, tilContrasena, tilConfirmar,
            tilFechaNacimiento, tilTutorNombre, tilTutorParentesco, tilTutorTelefono;
    private TextInputEditText etNombre, etCorreo, etContrasena, etConfirmar,
            etFechaNacimiento, etDisciplina, etTutorNombre, etTutorParentesco, etTutorTelefono;
    private AutoCompleteTextView spinnerRol;
    private LinearLayout layoutTutor;
    private View tilDisciplina;
    private Button btnCrearCuenta;
    private ProgressBar progressBar;
    private TextView tvYaTengo;

    // Fecha de nacimiento seleccionada
    private int anioNac, mesNac, diaNac;
    private boolean fechaNacSeleccionada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tilNombre    = findViewById(R.id.tilNombre);
        tilCorreo    = findViewById(R.id.tilCorreo);
        tilContrasena = findViewById(R.id.tilContrasena);
        tilConfirmar  = findViewById(R.id.tilConfirmar);
        tilFechaNacimiento = findViewById(R.id.tilFechaNacimiento);
        tilTutorNombre     = findViewById(R.id.tilTutorNombre);
        tilTutorParentesco = findViewById(R.id.tilTutorParentesco);
        tilTutorTelefono   = findViewById(R.id.tilTutorTelefono);
        etNombre     = findViewById(R.id.etNombre);
        etCorreo     = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        etConfirmar  = findViewById(R.id.etConfirmar);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        etDisciplina      = findViewById(R.id.etDisciplina);
        etTutorNombre     = findViewById(R.id.etTutorNombre);
        etTutorParentesco = findViewById(R.id.etTutorParentesco);
        etTutorTelefono   = findViewById(R.id.etTutorTelefono);
        spinnerRol   = findViewById(R.id.spinnerRol);
        layoutTutor  = findViewById(R.id.layoutTutor);
        tilDisciplina = findViewById(R.id.tilDisciplina);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        progressBar  = findViewById(R.id.progressBar);
        tvYaTengo    = findViewById(R.id.tvYaTengo);

        String[] roles = {getString(R.string.rol_atleta), getString(R.string.rol_padre)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, roles);
        spinnerRol.setAdapter(adapter);
        spinnerRol.setText(roles[0], false);

        // Mostrar/ocultar campos de atleta según el rol elegido
        spinnerRol.setOnItemClickListener((parent, view, position, id) -> aplicarVisibilidadPorRol());
        aplicarVisibilidadPorRol();

        etFechaNacimiento.setOnClickListener(v -> mostrarDatePicker());

        btnCrearCuenta.setOnClickListener(v -> doRegister());
        tvYaTengo.setOnClickListener(v -> finish());
    }

    private boolean esAtletaSeleccionado() {
        return !spinnerRol.getText().toString().equals(getString(R.string.rol_padre));
    }

    private void aplicarVisibilidadPorRol() {
        boolean atleta = esAtletaSeleccionado();
        int vis = atleta ? View.VISIBLE : View.GONE;
        tilFechaNacimiento.setVisibility(vis);
        tilDisciplina.setVisibility(vis);
        if (!atleta) layoutTutor.setVisibility(View.GONE);
        else evaluarMenorDeEdad();
    }

    private void mostrarDatePicker() {
        Calendar c = Calendar.getInstance();
        int y = fechaNacSeleccionada ? anioNac : c.get(Calendar.YEAR) - 12;
        int m = fechaNacSeleccionada ? mesNac  : c.get(Calendar.MONTH);
        int d = fechaNacSeleccionada ? diaNac  : c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dlg = new DatePickerDialog(this, (view, anio, mes, dia) -> {
            anioNac = anio; mesNac = mes; diaNac = dia;
            fechaNacSeleccionada = true;
            etFechaNacimiento.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", dia, mes + 1, anio));
            tilFechaNacimiento.setError(null);
            evaluarMenorDeEdad();
        }, y, m, d);
        dlg.getDatePicker().setMaxDate(System.currentTimeMillis());
        dlg.show();
    }

    /** Calcula la edad y muestra/oculta la sección del tutor si es menor de 18. */
    private void evaluarMenorDeEdad() {
        if (!fechaNacSeleccionada || !esAtletaSeleccionado()) {
            layoutTutor.setVisibility(View.GONE);
            return;
        }
        layoutTutor.setVisibility(calcularEdad() < 18 ? View.VISIBLE : View.GONE);
    }

    private int calcularEdad() {
        Calendar hoy = Calendar.getInstance();
        int edad = hoy.get(Calendar.YEAR) - anioNac;
        int mesActual = hoy.get(Calendar.MONTH);
        int diaActual = hoy.get(Calendar.DAY_OF_MONTH);
        if (mesActual < mesNac || (mesActual == mesNac && diaActual < diaNac)) edad--;
        return edad;
    }

    private String fechaNacIso() {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", anioNac, mesNac + 1, diaNac);
    }

    private void doRegister() {
        String nombre    = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
        String correo    = etCorreo.getText() != null ? etCorreo.getText().toString().trim() : "";
        String password  = etContrasena.getText() != null ? etContrasena.getText().toString() : "";
        String confirm   = etConfirmar.getText() != null ? etConfirmar.getText().toString() : "";

        tilNombre.setError(null);
        tilCorreo.setError(null);
        tilContrasena.setError(null);
        tilConfirmar.setError(null);
        tilFechaNacimiento.setError(null);
        tilTutorNombre.setError(null);
        tilTutorParentesco.setError(null);
        tilTutorTelefono.setError(null);

        if (nombre.isEmpty()) { tilNombre.setError(getString(R.string.err_campo_requerido)); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) { tilCorreo.setError(getString(R.string.err_correo_invalido)); return; }
        if (password.length() < 8) { tilContrasena.setError(getString(R.string.err_contrasena_corta)); return; }
        if (!password.matches(".*[A-Z].*") || !password.matches(".*[0-9].*")) {
            tilContrasena.setError(getString(R.string.err_contrasena_requisitos));
            return;
        }
        if (!password.equals(confirm)) { tilConfirmar.setError(getString(R.string.err_contrasenas_no_coinciden)); return; }

        boolean atleta = esAtletaSeleccionado();
        String rol = atleta ? "ATLETA" : "PADRE";

        RegisterRequest req = new RegisterRequest(nombre, correo, password, rol);

        if (atleta) {
            if (!fechaNacSeleccionada) {
                tilFechaNacimiento.setError(getString(R.string.err_fecha_nac_requerida));
                return;
            }
            req.setFechaNacimiento(fechaNacIso());
            String disc = etDisciplina.getText() != null ? etDisciplina.getText().toString().trim() : "";
            if (!disc.isEmpty()) req.setDisciplina(disc);

            // Si es menor de edad, los datos del tutor son obligatorios
            if (calcularEdad() < 18) {
                String tNombre = etTutorNombre.getText() != null ? etTutorNombre.getText().toString().trim() : "";
                String tParent = etTutorParentesco.getText() != null ? etTutorParentesco.getText().toString().trim() : "";
                String tTel    = etTutorTelefono.getText() != null ? etTutorTelefono.getText().toString().trim() : "";
                if (tNombre.isEmpty()) { tilTutorNombre.setError(getString(R.string.err_campo_requerido)); return; }
                if (tParent.isEmpty()) { tilTutorParentesco.setError(getString(R.string.err_campo_requerido)); return; }
                if (tTel.isEmpty())    { tilTutorTelefono.setError(getString(R.string.err_campo_requerido)); return; }
                req.setTutorNombre(tNombre);
                req.setTutorParentesco(tParent);
                req.setTutorTelefono(tTel);
            }
        }

        setLoading(true);

        ApiClient.getAuthService()
                .register(req)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        setLoading(false);
                        if (response.isSuccessful()) {
                            Intent intent = new Intent(RegisterActivity.this, VerificarCorreoActivity.class);
                            intent.putExtra("correo", correo);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "No se pudo registrar. Revisa los datos (o el correo ya existe).",
                                    Toast.LENGTH_LONG).show();
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
