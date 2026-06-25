package com.example.tallerappmovil.disciplinas;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.Disciplina;
import com.example.tallerappmovil.model.DisciplinaRequest;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearEditarDisciplinaActivity extends AppCompatActivity {

    private TextInputEditText etNombre, etDescripcion, etPesoMin, etPesoMax,
            etAlturaMin, etImcMin, etImcMax, etMasaMuscular, etGrasaMax;
    private Spinner spinnerUnidad;
    private SwitchMaterial switchEsTiempo, switchActiva;

    private Long disciplinaId = null;
    private static final String[] UNIDADES = {"s", "m", "pts"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_editar_disciplina);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etNombre       = findViewById(R.id.etNombre);
        etDescripcion  = findViewById(R.id.etDescripcion);
        etPesoMin      = findViewById(R.id.etPesoMin);
        etPesoMax      = findViewById(R.id.etPesoMax);
        etAlturaMin    = findViewById(R.id.etAlturaMin);
        etImcMin       = findViewById(R.id.etImcMin);
        etImcMax       = findViewById(R.id.etImcMax);
        etMasaMuscular = findViewById(R.id.etMasaMuscular);
        etGrasaMax     = findViewById(R.id.etGrasaMax);
        spinnerUnidad  = findViewById(R.id.spinnerUnidad);
        switchEsTiempo = findViewById(R.id.switchEsTiempo);
        switchActiva   = findViewById(R.id.switchActiva);

        ArrayAdapter<String> unidadAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, UNIDADES);
        unidadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnidad.setAdapter(unidadAdapter);

        disciplinaId = getIntent().getLongExtra("disciplinaId", -1L);
        if (disciplinaId == -1L) {
            disciplinaId = null;
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Nueva disciplina");
        } else {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Editar disciplina");
            findViewById(R.id.layoutEstado).setVisibility(View.VISIBLE);
            cargarDisciplina(disciplinaId);
        }

        findViewById(R.id.btnGuardar).setOnClickListener(v -> guardar());
    }

    private void cargarDisciplina(Long id) {
        ApiClient.getDisciplinasService().obtener(id).enqueue(new Callback<Disciplina>() {
            @Override
            public void onResponse(Call<Disciplina> c, Response<Disciplina> r) {
                if (r.isSuccessful() && r.body() != null) {
                    poblarFormulario(r.body());
                } else {
                    Toast.makeText(CrearEditarDisciplinaActivity.this,
                            "No se pudo cargar la disciplina", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Disciplina> c, Throwable t) {
                Toast.makeText(CrearEditarDisciplinaActivity.this,
                        "Error de conexión", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void poblarFormulario(Disciplina d) {
        etNombre.setText(d.getNombre());
        if (d.getDescripcion() != null) etDescripcion.setText(d.getDescripcion());
        switchEsTiempo.setChecked(d.isEsTiempo());
        switchActiva.setChecked(d.isActiva());

        String unidad = d.getUnidad();
        for (int i = 0; i < UNIDADES.length; i++) {
            if (UNIDADES[i].equals(unidad)) { spinnerUnidad.setSelection(i); break; }
        }

        setDouble(etPesoMin,      d.getPesoMinKg());
        setDouble(etPesoMax,      d.getPesoMaxKg());
        setDouble(etAlturaMin,    d.getAlturaMinCm());
        setDouble(etImcMin,       d.getImcMin());
        setDouble(etImcMax,       d.getImcMax());
        setDouble(etMasaMuscular, d.getMasaMuscularMinKg());
        setDouble(etGrasaMax,     d.getPorcentajeGrasaMax());
    }

    private void setDouble(TextInputEditText et, Double v) {
        if (v != null) et.setText(String.valueOf(v));
    }

    private void guardar() {
        String nombre = text(etNombre);
        if (nombre.isEmpty()) {
            etNombre.setError("Campo requerido");
            return;
        }
        String descripcion = text(etDescripcion);
        String unidad = UNIDADES[spinnerUnidad.getSelectedItemPosition()];
        boolean esTiempo = switchEsTiempo.isChecked();

        DisciplinaRequest req = new DisciplinaRequest(
                nombre, descripcion.isEmpty() ? null : descripcion,
                unidad, esTiempo,
                parseDouble(etPesoMin),
                parseDouble(etPesoMax),
                parseDouble(etAlturaMin),
                parseDouble(etImcMin),
                parseDouble(etImcMax),
                parseDouble(etMasaMuscular),
                parseDouble(etGrasaMax)
        );

        if (disciplinaId == null) {
            ApiClient.getDisciplinasService().crear(req).enqueue(new Callback<Disciplina>() {
                @Override
                public void onResponse(Call<Disciplina> c, Response<Disciplina> r) {
                    if (r.isSuccessful()) {
                        Toast.makeText(CrearEditarDisciplinaActivity.this,
                                "Disciplina creada", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CrearEditarDisciplinaActivity.this,
                                "Error al crear (¿nombre duplicado?)", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Disciplina> c, Throwable t) {
                    Toast.makeText(CrearEditarDisciplinaActivity.this,
                            "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            final Long id = disciplinaId;
            ApiClient.getDisciplinasService().actualizar(id, req).enqueue(new Callback<Disciplina>() {
                @Override
                public void onResponse(Call<Disciplina> c, Response<Disciplina> r) {
                    if (r.isSuccessful()) {
                        actualizarEstado(id, switchActiva.isChecked());
                    } else {
                        Toast.makeText(CrearEditarDisciplinaActivity.this,
                                "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Disciplina> c, Throwable t) {
                    Toast.makeText(CrearEditarDisciplinaActivity.this,
                            "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void actualizarEstado(Long id, boolean activa) {
        Map<String, Boolean> body = new HashMap<>();
        body.put("activa", activa);
        ApiClient.getDisciplinasService().cambiarEstado(id, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> c, Response<Void> r) {
                Toast.makeText(CrearEditarDisciplinaActivity.this,
                        "Disciplina actualizada", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<Void> c, Throwable t) {
                Toast.makeText(CrearEditarDisciplinaActivity.this,
                        "Guardado pero el estado no se actualizó", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private String text(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private Double parseDouble(TextInputEditText et) {
        String s = text(et);
        if (s.isEmpty()) return null;
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { return null; }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
