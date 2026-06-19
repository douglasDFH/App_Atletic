package com.example.tallerappmovil.marcas;

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
import com.example.tallerappmovil.model.AtletaInfo;
import com.example.tallerappmovil.model.MarcaPersonal;
import com.example.tallerappmovil.model.MarcaRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrarMarcaActivity extends AppCompatActivity {

    private TextInputLayout tilAtleta, tilDisciplina, tilResultado, tilUnidad, tilFecha;
    private AutoCompleteTextView spinnerAtleta, spinnerDisciplina, spinnerUnidad;
    private TextInputEditText etResultado, etFecha, etObservaciones;
    private MaterialButton btnGuardar;
    private ProgressBar progressBar;

    private List<AtletaInfo> atletasList = new ArrayList<>();
    private Long atletaSeleccionadoId = null;

    private int anio, mes, dia;

    private static final String[] DISCIPLINAS = {
            "100m", "200m", "400m", "Salto Largo", "Lanzamiento de Bala", "Gimnasia"
    };
    private static final String[] UNIDADES = {"s", "m", "pts"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_marca);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tilAtleta       = findViewById(R.id.tilAtleta);
        tilDisciplina   = findViewById(R.id.tilDisciplina);
        tilResultado    = findViewById(R.id.tilResultado);
        tilUnidad       = findViewById(R.id.tilUnidad);
        tilFecha        = findViewById(R.id.tilFecha);
        spinnerAtleta   = findViewById(R.id.spinnerAtleta);
        spinnerDisciplina = findViewById(R.id.spinnerDisciplina);
        spinnerUnidad   = findViewById(R.id.spinnerUnidad);
        etResultado     = findViewById(R.id.etResultado);
        etFecha         = findViewById(R.id.etFecha);
        etObservaciones = findViewById(R.id.etObservaciones);
        btnGuardar      = findViewById(R.id.btnGuardar);
        progressBar     = findViewById(R.id.progressBar);

        Calendar hoy = Calendar.getInstance();
        anio = hoy.get(Calendar.YEAR);
        mes  = hoy.get(Calendar.MONTH);
        dia  = hoy.get(Calendar.DAY_OF_MONTH);
        actualizarFecha();

        etFecha.setOnClickListener(v -> mostrarDatePicker());

        // Spinner disciplinas
        ArrayAdapter<String> discAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, DISCIPLINAS);
        spinnerDisciplina.setAdapter(discAdapter);
        spinnerDisciplina.setText(DISCIPLINAS[0], false);
        spinnerDisciplina.setOnItemClickListener((p, v, pos, id) ->
                autoSelectUnidad(DISCIPLINAS[pos]));

        // Spinner unidades
        ArrayAdapter<String> unidAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, UNIDADES);
        spinnerUnidad.setAdapter(unidAdapter);
        spinnerUnidad.setText("s", false);

        btnGuardar.setOnClickListener(v -> guardar());
        cargarAtletas();
    }

    private void cargarAtletas() {
        ApiClient.getMarcasService().getAtletas().enqueue(new Callback<List<AtletaInfo>>() {
            @Override
            public void onResponse(Call<List<AtletaInfo>> call, Response<List<AtletaInfo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    atletasList = response.body();
                    List<String> nombres = new ArrayList<>();
                    for (AtletaInfo a : atletasList) nombres.add(a.getNombreCompleto());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            RegistrarMarcaActivity.this,
                            android.R.layout.simple_dropdown_item_1line, nombres);
                    spinnerAtleta.setAdapter(adapter);
                    if (!atletasList.isEmpty()) {
                        spinnerAtleta.setText(atletasList.get(0).getNombreCompleto(), false);
                        atletaSeleccionadoId = atletasList.get(0).getId();
                    }
                    spinnerAtleta.setOnItemClickListener((parent, view, position, id) ->
                            atletaSeleccionadoId = atletasList.get(position).getId());
                }
            }
            @Override
            public void onFailure(Call<List<AtletaInfo>> call, Throwable t) {
                Toast.makeText(RegistrarMarcaActivity.this,
                        getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void autoSelectUnidad(String disciplina) {
        if (disciplina.endsWith("m") || disciplina.equals("Salto Largo")
                || disciplina.equals("Lanzamiento de Bala")) {
            spinnerUnidad.setText(disciplina.endsWith("m") ? "s" : "m", false);
        } else if (disciplina.equals("Gimnasia")) {
            spinnerUnidad.setText("pts", false);
        }
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
        tilAtleta.setError(null);
        tilDisciplina.setError(null);
        tilResultado.setError(null);

        if (atletaSeleccionadoId == null) {
            tilAtleta.setError(getString(R.string.err_campo_requerido)); return;
        }
        String disciplina = spinnerDisciplina.getText().toString().trim();
        if (disciplina.isEmpty()) {
            tilDisciplina.setError(getString(R.string.err_campo_requerido)); return;
        }
        String resultado = etResultado.getText() != null
                ? etResultado.getText().toString().trim() : "";
        if (resultado.isEmpty()) {
            tilResultado.setError(getString(R.string.err_campo_requerido)); return;
        }

        String unidad = spinnerUnidad.getText().toString().trim();
        String fechaIso = String.format(Locale.getDefault(), "%04d-%02d-%02d", anio, mes + 1, dia);
        String obs = etObservaciones.getText() != null
                ? etObservaciones.getText().toString().trim() : "";

        MarcaRequest req = new MarcaRequest(atletaSeleccionadoId, disciplina,
                resultado, unidad, fechaIso, obs);

        setLoading(true);
        ApiClient.getMarcasService().registrarMarca(req)
                .enqueue(new Callback<MarcaPersonal>() {
                    @Override
                    public void onResponse(Call<MarcaPersonal> call, Response<MarcaPersonal> response) {
                        setLoading(false);
                        if (response.isSuccessful()) {
                            boolean esPR = response.body() != null && response.body().isEsMejorMarca();
                            String msg = getString(R.string.msg_marca_guardada);
                            if (esPR) msg += " " + getString(R.string.msg_marca_es_pr);
                            Toast.makeText(RegistrarMarcaActivity.this, msg, Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(RegistrarMarcaActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<MarcaPersonal> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(RegistrarMarcaActivity.this,
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
