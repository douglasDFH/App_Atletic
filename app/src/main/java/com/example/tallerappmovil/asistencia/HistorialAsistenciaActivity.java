package com.example.tallerappmovil.asistencia;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.AsistenciaHistorial;
import com.example.tallerappmovil.utils.PdfReportGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistorialAsistenciaActivity extends AppCompatActivity {

    public static final String EXTRA_ATLETA_ID     = "atletaId";
    public static final String EXTRA_ATLETA_NOMBRE = "atletaNombre";

    private HistorialAsistenciaAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvVacio;
    private TextView tvCountPresente, tvCountAusente, tvCountJustificado, tvPorcentaje;
    private String atletaNombreLocal;
    private int cntPresentes, cntAusentes, cntJustificados;

    private static final String[] FILTROS_LABELS =
            {"Todas", "Presente", "Ausente", "Justificado"};
    private static final String[] FILTROS_API =
            {null, "PRESENTE", "AUSENTE", "JUSTIFICADO"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_asistencia);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Long atletaId       = getIntent().getLongExtra(EXTRA_ATLETA_ID, -1L);
        atletaNombreLocal   = getIntent().getStringExtra(EXTRA_ATLETA_NOMBRE);

        if (atletaNombreLocal != null && getSupportActionBar() != null) {
            getSupportActionBar().setTitle(atletaNombreLocal);
        }

        progressBar         = findViewById(R.id.progressBar);
        tvVacio             = findViewById(R.id.tvVacio);
        tvCountPresente     = findViewById(R.id.tvCountPresente);
        tvCountAusente      = findViewById(R.id.tvCountAusente);
        tvCountJustificado  = findViewById(R.id.tvCountJustificado);
        tvPorcentaje        = findViewById(R.id.tvPorcentaje);

        RecyclerView recycler = findViewById(R.id.recyclerHistorial);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistorialAsistenciaAdapter();
        recycler.setAdapter(adapter);

        setupSpinnerFiltro();

        if (atletaId != -1L) {
            cargarHistorial(atletaId);
        } else {
            cargarMiHistorial();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_export, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_export_pdf) {
            java.util.List<AsistenciaHistorial> todos = adapter.getAllItems();
            if (todos == null || todos.isEmpty()) {
                Toast.makeText(this, "No hay datos para exportar", Toast.LENGTH_SHORT).show();
            } else {
                PdfReportGenerator.exportarHistorialAtleta(this, todos,
                        atletaNombreLocal != null ? atletaNombreLocal : "Atleta",
                        cntPresentes, cntAusentes, cntJustificados);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSpinnerFiltro() {
        Spinner spinner = findViewById(R.id.spinnerFiltro);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, FILTROS_LABELS);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                adapter.aplicarFiltro(FILTROS_API[pos]);
                tvVacio.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void cargarHistorial(Long atletaId) {
        progressBar.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);

        ApiClient.getAsistenciaService().getHistorialAtleta(atletaId)
                .enqueue(new Callback<List<AsistenciaHistorial>>() {
                    @Override
                    public void onResponse(Call<List<AsistenciaHistorial>> call,
                                           Response<List<AsistenciaHistorial>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            mostrarResultados(response.body());
                        } else {
                            tvVacio.setVisibility(View.VISIBLE);
                            Toast.makeText(HistorialAsistenciaActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<AsistenciaHistorial>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvVacio.setVisibility(View.VISIBLE);
                        Toast.makeText(HistorialAsistenciaActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarMiHistorial() {
        progressBar.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);

        ApiClient.getAsistenciaService().getMiHistorial()
                .enqueue(new Callback<List<AsistenciaHistorial>>() {
                    @Override
                    public void onResponse(Call<List<AsistenciaHistorial>> call,
                                           Response<List<AsistenciaHistorial>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            mostrarResultados(response.body());
                        } else {
                            tvVacio.setVisibility(View.VISIBLE);
                            Toast.makeText(HistorialAsistenciaActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<AsistenciaHistorial>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvVacio.setVisibility(View.VISIBLE);
                        Toast.makeText(HistorialAsistenciaActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarResultados(List<AsistenciaHistorial> lista) {
        adapter.setItems(lista);
        tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);

        int presente = 0, ausente = 0, justificado = 0;
        for (AsistenciaHistorial h : lista) {
            if ("PRESENTE".equals(h.getEstado()))         presente++;
            else if ("AUSENTE".equals(h.getEstado()))     ausente++;
            else if ("JUSTIFICADO".equals(h.getEstado())) justificado++;
        }

        cntPresentes   = presente;
        cntAusentes    = ausente;
        cntJustificados = justificado;

        tvCountPresente.setText(String.valueOf(presente));
        tvCountAusente.setText(String.valueOf(ausente));
        tvCountJustificado.setText(String.valueOf(justificado));

        int total = lista.size();
        if (total > 0) {
            int pct = Math.round(presente * 100f / total);
            tvPorcentaje.setText(pct + "%");
        } else {
            tvPorcentaje.setText("--");
        }
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
