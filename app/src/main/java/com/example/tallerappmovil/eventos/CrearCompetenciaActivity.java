package com.example.tallerappmovil.eventos;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.Competencia;
import com.example.tallerappmovil.model.CompetenciaRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearCompetenciaActivity extends AppCompatActivity {

    private TextInputLayout tilNombre, tilDisciplina, tilFecha, tilLugar;
    private TextInputEditText etNombre, etFecha, etLugar, etDescripcion;
    private AutoCompleteTextView spinnerDisciplina, spinnerCategoria;
    private MaterialButton btnGuardar;
    private ProgressBar progressBar;

    private int anio, mes, dia;

    private static final String[] DISCIPLINAS = {
            "100m", "200m", "400m", "Salto Largo", "Lanzamiento de Bala", "Gimnasia", "Múltiples"
    };
    private static final String[] CATEGORIAS = {
            "Juvenil A", "Juvenil B", "Sub-18", "Sub-20", "Senior", "Máster", "Abierta"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_competencia);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tilNombre      = findViewById(R.id.tilNombre);
        tilDisciplina  = findViewById(R.id.tilDisciplina);
        tilFecha       = findViewById(R.id.tilFecha);
        tilLugar       = findViewById(R.id.tilLugar);
        etNombre       = findViewById(R.id.etNombre);
        etFecha        = findViewById(R.id.etFecha);
        etLugar        = findViewById(R.id.etLugar);
        etDescripcion  = findViewById(R.id.etDescripcion);
        spinnerDisciplina = findViewById(R.id.spinnerDisciplina);
        spinnerCategoria  = findViewById(R.id.spinnerCategoria);
        btnGuardar     = findViewById(R.id.btnGuardar);
        progressBar    = findViewById(R.id.progressBar);

        Calendar hoy = Calendar.getInstance();
        anio = hoy.get(Calendar.YEAR);
        mes  = hoy.get(Calendar.MONTH);
        dia  = hoy.get(Calendar.DAY_OF_MONTH);
        actualizarFecha();

        etFecha.setOnClickListener(v -> mostrarDatePicker());

        ArrayAdapter<String> discAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, DISCIPLINAS);
        spinnerDisciplina.setAdapter(discAdapter);
        spinnerDisciplina.setText(DISCIPLINAS[0], false);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, CATEGORIAS);
        spinnerCategoria.setAdapter(catAdapter);
        spinnerCategoria.setText(CATEGORIAS[0], false);

        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void mostrarDatePicker() {
        new DatePickerDialog(this, (view, y, m, d) -> {
            anio = y; mes = m; dia = d;
            actualizarFecha();
        }, anio, mes, dia).show();
    }

    private void actualizarFecha() {
        etFecha.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", dia, mes + 1, anio));
    }

    private void guardar() {
        tilNombre.setError(null);
        tilDisciplina.setError(null);
        tilFecha.setError(null);
        tilLugar.setError(null);

        String nombre = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
        if (nombre.isEmpty()) { tilNombre.setError(getString(R.string.err_campo_requerido)); return; }

        String disciplina = spinnerDisciplina.getText().toString().trim();
        if (disciplina.isEmpty()) { tilDisciplina.setError(getString(R.string.err_campo_requerido)); return; }

        String lugar = etLugar.getText() != null ? etLugar.getText().toString().trim() : "";
        if (lugar.isEmpty()) { tilLugar.setError(getString(R.string.err_campo_requerido)); return; }

        String fechaIso = String.format(Locale.getDefault(), "%04d-%02d-%02d", anio, mes + 1, dia);
        String categoria = spinnerCategoria.getText().toString().trim();
        String descripcion = etDescripcion.getText() != null
                ? etDescripcion.getText().toString().trim() : "";

        CompetenciaRequest req = new CompetenciaRequest(nombre, disciplina, fechaIso,
                lugar, categoria, descripcion);

        setLoading(true);
        ApiClient.getCompetenciasService().crearCompetencia(req)
                .enqueue(new Callback<Competencia>() {
                    @Override
                    public void onResponse(Call<Competencia> call, Response<Competencia> response) {
                        setLoading(false);
                        if (response.isSuccessful()) {
                            Toast.makeText(CrearCompetenciaActivity.this,
                                    getString(R.string.msg_competencia_creada), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(CrearCompetenciaActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Competencia> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(CrearCompetenciaActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnGuardar.setEnabled(!loading);
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
