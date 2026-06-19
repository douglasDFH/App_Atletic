package com.example.tallerappmovil.asistencia;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.GrupoEntrenamiento;
import com.example.tallerappmovil.model.ReporteAtleta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReporteAsistenciaActivity extends AppCompatActivity {

    public static final String EXTRA_GRUPO_ID     = "grupo_id";
    public static final String EXTRA_GRUPO_NOMBRE = "grupo_nombre";

    private Spinner spinnerGrupo, spinnerMes;
    private RecyclerView recycler;
    private View progressBar, layoutResumen;
    private TextView tvVacio, tvResumenAtletas, tvResumenPromedio;

    private final List<ReporteAtleta> lista = new ArrayList<>();
    private ReporteAsistenciaAdapter adapter;

    private final List<GrupoEntrenamiento> grupos = new ArrayList<>();
    private final List<String> gruposLabels = new ArrayList<>();
    private final List<String> mesesLabels  = new ArrayList<>();
    private final List<String> mesesApi     = new ArrayList<>();

    private Long grupoIdInicial = null;
    private boolean spinnerGrupoReady = false;
    private boolean spinnerMesReady   = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_asistencia);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        grupoIdInicial = getIntent().getLongExtra(EXTRA_GRUPO_ID, -1L);
        if (grupoIdInicial == -1L) grupoIdInicial = null;

        spinnerGrupo    = findViewById(R.id.spinnerGrupo);
        spinnerMes      = findViewById(R.id.spinnerMes);
        recycler        = findViewById(R.id.recyclerReporte);
        progressBar     = findViewById(R.id.progressBar);
        tvVacio         = findViewById(R.id.tvVacio);
        layoutResumen   = findViewById(R.id.layoutResumen);
        tvResumenAtletas = findViewById(R.id.tvResumenAtletas);
        tvResumenPromedio= findViewById(R.id.tvResumenPromedio);

        adapter = new ReporteAsistenciaAdapter(lista);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        generarMeses();
        configurarSpinnerMes();
        cargarGrupos();
    }

    private void generarMeses() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat labelFmt = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
        SimpleDateFormat apiFmt   = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        for (int i = 0; i < 6; i++) {
            String label = labelFmt.format(cal.getTime());
            // Capitalizar primera letra
            mesesLabels.add(label.substring(0, 1).toUpperCase() + label.substring(1));
            mesesApi.add(apiFmt.format(cal.getTime()));
            cal.add(Calendar.MONTH, -1);
        }
    }

    private void configurarSpinnerMes() {
        ArrayAdapter<String> adapterMes = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mesesLabels);
        adapterMes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMes.setAdapter(adapterMes);
        spinnerMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                if (spinnerGrupoReady) cargarReporte();
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
        spinnerMesReady = true;
    }

    private void cargarGrupos() {
        ApiClient.getGruposService().listarGrupos()
                .enqueue(new Callback<List<GrupoEntrenamiento>>() {
                    @Override
                    public void onResponse(Call<List<GrupoEntrenamiento>> call,
                                           Response<List<GrupoEntrenamiento>> response) {
                        grupos.clear();
                        gruposLabels.clear();
                        gruposLabels.add("Todos los grupos");
                        grupos.add(null); // representación de "sin filtro"

                        if (response.isSuccessful() && response.body() != null) {
                            grupos.addAll(response.body());
                            for (GrupoEntrenamiento g : response.body()) {
                                gruposLabels.add(g.getNombre());
                            }
                        }

                        ArrayAdapter<String> ag = new ArrayAdapter<>(ReporteAsistenciaActivity.this,
                                android.R.layout.simple_spinner_item, gruposLabels);
                        ag.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerGrupo.setAdapter(ag);

                        // Pre-seleccionar grupo si viene del intent
                        int selIdx = 0;
                        if (grupoIdInicial != null) {
                            for (int i = 1; i < grupos.size(); i++) {
                                if (grupos.get(i) != null &&
                                        grupoIdInicial.equals(grupos.get(i).getId())) {
                                    selIdx = i;
                                    break;
                                }
                            }
                        }
                        final int finalSelIdx = selIdx;
                        spinnerGrupoReady = true;
                        spinnerGrupo.setSelection(finalSelIdx, false);

                        spinnerGrupo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                                cargarReporte();
                            }
                            @Override public void onNothingSelected(AdapterView<?> p) {}
                        });

                        cargarReporte();
                    }

                    @Override
                    public void onFailure(Call<List<GrupoEntrenamiento>> call, Throwable t) {
                        spinnerGrupoReady = true;
                        ArrayAdapter<String> ag = new ArrayAdapter<>(ReporteAsistenciaActivity.this,
                                android.R.layout.simple_spinner_item, gruposLabels);
                        ag.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerGrupo.setAdapter(ag);
                        spinnerGrupo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                                cargarReporte();
                            }
                            @Override public void onNothingSelected(AdapterView<?> p) {}
                        });
                    }
                });
    }

    private void cargarReporte() {
        int grupoIdx = spinnerGrupo.getSelectedItemPosition();
        int mesIdx   = spinnerMes.getSelectedItemPosition();
        if (mesIdx < 0 || mesIdx >= mesesApi.size()) return;

        Long grupoId = null;
        if (grupoIdx > 0 && grupoIdx < grupos.size() && grupos.get(grupoIdx) != null) {
            grupoId = grupos.get(grupoIdx).getId();
        }
        String mes = mesesApi.get(mesIdx);

        progressBar.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);
        layoutResumen.setVisibility(View.GONE);

        ApiClient.getAsistenciaService().getReporte(grupoId, mes)
                .enqueue(new Callback<List<ReporteAtleta>>() {
                    @Override
                    public void onResponse(Call<List<ReporteAtleta>> call,
                                           Response<List<ReporteAtleta>> response) {
                        progressBar.setVisibility(View.GONE);
                        lista.clear();
                        if (response.isSuccessful() && response.body() != null) {
                            lista.addAll(response.body());
                        }
                        adapter.notifyDataSetChanged();

                        if (lista.isEmpty()) {
                            tvVacio.setVisibility(View.VISIBLE);
                        } else {
                            mostrarResumen();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ReporteAtleta>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvVacio.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void mostrarResumen() {
        if (lista.isEmpty()) return;
        float sumaTotal = 0;
        for (ReporteAtleta r : lista) {
            float pct = r.getPorcentajeAsistencia();
            if (pct == 0 && r.getTotalSesiones() > 0) {
                pct = r.getPresentes() * 100f / r.getTotalSesiones();
            }
            sumaTotal += pct;
        }
        int promedio = Math.round(sumaTotal / lista.size());
        tvResumenAtletas.setText(lista.size() + " atleta" + (lista.size() == 1 ? "" : "s"));
        tvResumenPromedio.setText("Promedio grupo: " + promedio + "%");
        layoutResumen.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
