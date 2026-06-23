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
import com.example.tallerappmovil.model.GrupoEntrenamiento;
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

public class CrearCompetenciaActivity extends AppCompatActivity {

    public static final String EXTRA_COMPETENCIA_ID    = "competenciaId";
    public static final String EXTRA_COMPETENCIA_NOMBRE = "competenciaNombre";
    public static final String EXTRA_DISCIPLINA        = "disciplina";
    public static final String EXTRA_FECHA             = "fecha";
    public static final String EXTRA_LUGAR             = "lugar";
    public static final String EXTRA_CATEGORIA         = "categoria";
    public static final String EXTRA_DESCRIPCION       = "descripcion";

    private TextInputLayout tilNombre, tilDisciplina, tilFecha, tilLugar;
    private TextInputEditText etNombre, etFecha, etLugar, etDescripcion;
    private AutoCompleteTextView spinnerDisciplina, spinnerCategoria, spinnerGrupoConvocado;
    private MaterialButton btnGuardar;
    private ProgressBar progressBar;

    private int anio, mes, dia;
    private Long competenciaId = null;
    private List<GrupoEntrenamiento> grupos = new ArrayList<>();

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
        spinnerDisciplina    = findViewById(R.id.spinnerDisciplina);
        spinnerCategoria     = findViewById(R.id.spinnerCategoria);
        spinnerGrupoConvocado = findViewById(R.id.spinnerGrupoConvocado);
        btnGuardar     = findViewById(R.id.btnGuardar);
        progressBar    = findViewById(R.id.progressBar);

        ArrayAdapter<String> discAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, DISCIPLINAS);
        spinnerDisciplina.setAdapter(discAdapter);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, CATEGORIAS);
        spinnerCategoria.setAdapter(catAdapter);

        etFecha.setOnClickListener(v -> mostrarDatePicker());
        btnGuardar.setOnClickListener(v -> guardar());
        cargarGrupos();

        // Modo edición si viene con id
        if (getIntent().hasExtra(EXTRA_COMPETENCIA_ID)) {
            competenciaId = getIntent().getLongExtra(EXTRA_COMPETENCIA_ID, -1L);
            if (competenciaId == -1L) competenciaId = null;
        }

        if (competenciaId != null) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(getString(R.string.lbl_editar_competencia));
            btnGuardar.setText(getString(R.string.btn_guardar_competencia));
            precargarCampos();
        } else {
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(getString(R.string.lbl_crear_competencia));
            Calendar hoy = Calendar.getInstance();
            anio = hoy.get(Calendar.YEAR);
            mes  = hoy.get(Calendar.MONTH);
            dia  = hoy.get(Calendar.DAY_OF_MONTH);
            actualizarFechaDisplay();
            spinnerDisciplina.setText(DISCIPLINAS[0], false);
            spinnerCategoria.setText(CATEGORIAS[0], false);
        }
    }

    private void precargarCampos() {
        String nombre    = getIntent().getStringExtra(EXTRA_COMPETENCIA_NOMBRE);
        String disciplina = getIntent().getStringExtra(EXTRA_DISCIPLINA);
        String fechaIso  = getIntent().getStringExtra(EXTRA_FECHA);
        String lugar     = getIntent().getStringExtra(EXTRA_LUGAR);
        String categoria = getIntent().getStringExtra(EXTRA_CATEGORIA);
        String descripcion = getIntent().getStringExtra(EXTRA_DESCRIPCION);

        if (nombre != null)     etNombre.setText(nombre);
        if (lugar != null)      etLugar.setText(lugar);
        if (descripcion != null) etDescripcion.setText(descripcion);
        if (disciplina != null) spinnerDisciplina.setText(disciplina, false);
        if (categoria != null)  spinnerCategoria.setText(categoria, false);

        if (fechaIso != null && fechaIso.length() == 10) {
            try {
                anio = Integer.parseInt(fechaIso.substring(0, 4));
                mes  = Integer.parseInt(fechaIso.substring(5, 7)) - 1;
                dia  = Integer.parseInt(fechaIso.substring(8, 10));
            } catch (NumberFormatException ignored) {
                Calendar hoy = Calendar.getInstance();
                anio = hoy.get(Calendar.YEAR);
                mes  = hoy.get(Calendar.MONTH);
                dia  = hoy.get(Calendar.DAY_OF_MONTH);
            }
        } else {
            Calendar hoy = Calendar.getInstance();
            anio = hoy.get(Calendar.YEAR);
            mes  = hoy.get(Calendar.MONTH);
            dia  = hoy.get(Calendar.DAY_OF_MONTH);
        }
        actualizarFechaDisplay();
    }

    private void mostrarDatePicker() {
        new DatePickerDialog(this, (view, y, m, d) -> {
            anio = y; mes = m; dia = d;
            actualizarFechaDisplay();
        }, anio, mes, dia).show();
    }

    private void actualizarFechaDisplay() {
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

        String fechaIso   = String.format(Locale.getDefault(), "%04d-%02d-%02d", anio, mes + 1, dia);
        String categoria  = spinnerCategoria.getText().toString().trim();
        String descripcion = etDescripcion.getText() != null
                ? etDescripcion.getText().toString().trim() : "";

        // Grupo convocado opcional (HU-09)
        Long grupoSeleccionadoId = null;
        String grupoTexto = spinnerGrupoConvocado.getText().toString().trim();
        for (GrupoEntrenamiento g : grupos) {
            if (g.getNombre().equals(grupoTexto)) { grupoSeleccionadoId = g.getId(); break; }
        }

        CompetenciaRequest req = new CompetenciaRequest(nombre, disciplina, fechaIso,
                lugar, categoria, descripcion, grupoSeleccionadoId);

        setLoading(true);

        if (competenciaId != null) {
            ApiClient.getCompetenciasService().editarCompetencia(competenciaId, req)
                    .enqueue(new Callback<Competencia>() {
                        @Override
                        public void onResponse(Call<Competencia> call, Response<Competencia> response) {
                            setLoading(false);
                            if (response.isSuccessful()) {
                                Toast.makeText(CrearCompetenciaActivity.this,
                                        getString(R.string.msg_competencia_actualizada),
                                        Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
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
        } else {
            ApiClient.getCompetenciasService().crearCompetencia(req)
                    .enqueue(new Callback<Competencia>() {
                        @Override
                        public void onResponse(Call<Competencia> call, Response<Competencia> response) {
                            setLoading(false);
                            if (response.isSuccessful()) {
                                Toast.makeText(CrearCompetenciaActivity.this,
                                        getString(R.string.msg_competencia_creada),
                                        Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
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
    }

    private void cargarGrupos() {
        ApiClient.getAgendaService().listarGrupos().enqueue(new Callback<List<GrupoEntrenamiento>>() {
            @Override
            public void onResponse(Call<List<GrupoEntrenamiento>> call,
                                   Response<List<GrupoEntrenamiento>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    grupos = response.body();
                    List<String> nombres = new ArrayList<>();
                    nombres.add(""); // opción vacía = todos
                    for (GrupoEntrenamiento g : grupos) nombres.add(g.getNombre());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            CrearCompetenciaActivity.this,
                            android.R.layout.simple_dropdown_item_1line, nombres);
                    spinnerGrupoConvocado.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<GrupoEntrenamiento>> call, Throwable t) {}
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnGuardar.setEnabled(!loading);
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
