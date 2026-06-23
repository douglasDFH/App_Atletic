package com.example.tallerappmovil.atletas;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.AtletaCrearRequest;
import com.example.tallerappmovil.model.AtletaDetalle;
import com.example.tallerappmovil.model.AtletaEditarRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarAtletaActivity extends AppCompatActivity {

    public static final String EXTRA_ATLETA_ID = "atletaId";

    private TextInputLayout tilNombre, tilCorreo, tilContrasena, tilFechaNacimiento,
            tilTutorNombre, tilTutorParentesco, tilTutorTelefono;
    private TextInputEditText etNombre, etCorreo, etContrasena, etDisciplina, etCategoria,
            etFechaNacimiento, etTutorNombre, etTutorParentesco, etTutorTelefono;
    private LinearLayout layoutTutor;
    private MaterialButton btnGuardar;
    private ProgressBar progressBar;

    private Long atletaId; // null = crear
    private int anioNac, mesNac, diaNac;
    private boolean fechaNacSeleccionada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_atleta);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tilNombre     = findViewById(R.id.tilNombre);
        tilCorreo     = findViewById(R.id.tilCorreo);
        tilContrasena = findViewById(R.id.tilContrasena);
        tilFechaNacimiento = findViewById(R.id.tilFechaNacimiento);
        tilTutorNombre     = findViewById(R.id.tilTutorNombre);
        tilTutorParentesco = findViewById(R.id.tilTutorParentesco);
        tilTutorTelefono   = findViewById(R.id.tilTutorTelefono);
        etNombre      = findViewById(R.id.etNombre);
        etCorreo      = findViewById(R.id.etCorreo);
        etContrasena  = findViewById(R.id.etContrasena);
        etDisciplina  = findViewById(R.id.etDisciplina);
        etCategoria   = findViewById(R.id.etCategoria);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        etTutorNombre     = findViewById(R.id.etTutorNombre);
        etTutorParentesco = findViewById(R.id.etTutorParentesco);
        etTutorTelefono   = findViewById(R.id.etTutorTelefono);
        layoutTutor   = findViewById(R.id.layoutTutor);
        btnGuardar    = findViewById(R.id.btnGuardar);
        progressBar   = findViewById(R.id.progressBar);

        long id = getIntent().getLongExtra(EXTRA_ATLETA_ID, -1L);
        atletaId = id != -1L ? id : null;

        boolean esCrear = atletaId == null;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(esCrear
                    ? R.string.lbl_nuevo_atleta : R.string.lbl_editar_atleta));
        }
        // Correo y contraseña solo al crear
        tilCorreo.setVisibility(esCrear ? View.VISIBLE : View.GONE);
        tilContrasena.setVisibility(esCrear ? View.VISIBLE : View.GONE);

        etFechaNacimiento.setOnClickListener(v -> mostrarDatePicker());
        btnGuardar.setOnClickListener(v -> guardar());

        if (!esCrear) cargarDetalle();
    }

    private void cargarDetalle() {
        ApiClient.getAtletasService().getAtleta(atletaId).enqueue(new Callback<AtletaDetalle>() {
            @Override
            public void onResponse(Call<AtletaDetalle> call, Response<AtletaDetalle> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AtletaDetalle d = response.body();
                    etNombre.setText(d.getNombreCompleto());
                    etDisciplina.setText(d.getDisciplina());
                    etCategoria.setText(d.getCategoria());
                    if (d.getTutorNombre() != null) etTutorNombre.setText(d.getTutorNombre());
                    if (d.getTutorParentesco() != null) etTutorParentesco.setText(d.getTutorParentesco());
                    if (d.getTutorTelefono() != null) etTutorTelefono.setText(d.getTutorTelefono());
                    prefillFecha(d.getFechaNacimiento());
                }
            }
            @Override public void onFailure(Call<AtletaDetalle> call, Throwable t) {}
        });
    }

    private void prefillFecha(String iso) {
        if (iso == null || iso.length() < 10) return;
        try {
            anioNac = Integer.parseInt(iso.substring(0, 4));
            mesNac  = Integer.parseInt(iso.substring(5, 7)) - 1;
            diaNac  = Integer.parseInt(iso.substring(8, 10));
            fechaNacSeleccionada = true;
            etFechaNacimiento.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", diaNac, mesNac + 1, anioNac));
            evaluarMenor();
        } catch (Exception ignored) {}
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
            evaluarMenor();
        }, y, m, d);
        dlg.getDatePicker().setMaxDate(System.currentTimeMillis());
        dlg.show();
    }

    private void evaluarMenor() {
        layoutTutor.setVisibility(fechaNacSeleccionada && calcularEdad() < 18 ? View.VISIBLE : View.GONE);
    }

    private int calcularEdad() {
        Calendar hoy = Calendar.getInstance();
        int edad = hoy.get(Calendar.YEAR) - anioNac;
        if (hoy.get(Calendar.MONTH) < mesNac
                || (hoy.get(Calendar.MONTH) == mesNac && hoy.get(Calendar.DAY_OF_MONTH) < diaNac)) edad--;
        return edad;
    }

    private String fechaIso() {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", anioNac, mesNac + 1, diaNac);
    }

    private void guardar() {
        String nombre = txt(etNombre);
        tilNombre.setError(null); tilCorreo.setError(null); tilContrasena.setError(null);
        tilTutorNombre.setError(null); tilTutorParentesco.setError(null); tilTutorTelefono.setError(null);

        if (nombre.isEmpty()) { tilNombre.setError(getString(R.string.err_campo_requerido)); return; }

        // Si es menor, exigir datos del tutor
        boolean menor = fechaNacSeleccionada && calcularEdad() < 18;
        if (menor) {
            if (txt(etTutorNombre).isEmpty())     { tilTutorNombre.setError(getString(R.string.err_campo_requerido)); return; }
            if (txt(etTutorParentesco).isEmpty())  { tilTutorParentesco.setError(getString(R.string.err_campo_requerido)); return; }
            if (txt(etTutorTelefono).isEmpty())    { tilTutorTelefono.setError(getString(R.string.err_campo_requerido)); return; }
        }

        setLoading(true);

        if (atletaId == null) {
            String correo = txt(etCorreo);
            String pass   = txt(etContrasena);
            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                setLoading(false); tilCorreo.setError(getString(R.string.err_correo_invalido)); return;
            }
            if (pass.length() < 8) {
                setLoading(false); tilContrasena.setError(getString(R.string.err_contrasena_corta)); return;
            }
            AtletaCrearRequest req = new AtletaCrearRequest(nombre, correo, pass);
            llenarComunes(req::setDisciplina, req::setCategoria, req::setFechaNacimiento,
                    req::setTutorNombre, req::setTutorParentesco, req::setTutorTelefono, menor);
            ApiClient.getAtletasService().crearAtleta(req).enqueue(callback(R.string.msg_atleta_creado));
        } else {
            AtletaEditarRequest req = new AtletaEditarRequest();
            req.setNombreCompleto(nombre);
            llenarComunes(req::setDisciplina, req::setCategoria, req::setFechaNacimiento,
                    req::setTutorNombre, req::setTutorParentesco, req::setTutorTelefono, menor);
            ApiClient.getAtletasService().editarAtleta(atletaId, req)
                    .enqueue(callbackDetalle(R.string.msg_atleta_actualizado));
        }
    }

    private interface Setter { void set(String v); }

    private void llenarComunes(Setter disc, Setter cat, Setter fecha,
                               Setter tNom, Setter tPar, Setter tTel, boolean menor) {
        String d = txt(etDisciplina); if (!d.isEmpty()) disc.set(d);
        String c = txt(etCategoria);  if (!c.isEmpty()) cat.set(c);
        if (fechaNacSeleccionada) fecha.set(fechaIso());
        if (menor) {
            tNom.set(txt(etTutorNombre));
            tPar.set(txt(etTutorParentesco));
            tTel.set(txt(etTutorTelefono));
        }
    }

    private Callback<Void> callback(int msgOk) {
        return new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {
                setLoading(false);
                if (response.isSuccessful()) { Toast.makeText(EditarAtletaActivity.this, getString(msgOk), Toast.LENGTH_SHORT).show(); finish(); }
                else Toast.makeText(EditarAtletaActivity.this, "No se pudo guardar. Revisa los datos.", Toast.LENGTH_LONG).show();
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                setLoading(false);
                Toast.makeText(EditarAtletaActivity.this, getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
            }
        };
    }

    private Callback<AtletaDetalle> callbackDetalle(int msgOk) {
        return new Callback<AtletaDetalle>() {
            @Override public void onResponse(Call<AtletaDetalle> call, Response<AtletaDetalle> response) {
                setLoading(false);
                if (response.isSuccessful()) { Toast.makeText(EditarAtletaActivity.this, getString(msgOk), Toast.LENGTH_SHORT).show(); finish(); }
                else Toast.makeText(EditarAtletaActivity.this, "No se pudo guardar. Revisa los datos.", Toast.LENGTH_LONG).show();
            }
            @Override public void onFailure(Call<AtletaDetalle> call, Throwable t) {
                setLoading(false);
                Toast.makeText(EditarAtletaActivity.this, getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
            }
        };
    }

    private String txt(TextInputEditText e) {
        return e.getText() != null ? e.getText().toString().trim() : "";
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnGuardar.setEnabled(!loading);
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
