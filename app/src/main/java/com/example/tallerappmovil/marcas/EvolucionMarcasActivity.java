package com.example.tallerappmovil.marcas;

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
import com.example.tallerappmovil.model.MarcaPersonal;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EvolucionMarcasActivity extends AppCompatActivity {

    public static final String EXTRA_DISCIPLINA = "disciplina";
    public static final String EXTRA_ATLETA_ID  = "atleta_id";

    private static final String[] DISCIPLINAS = {
            "100m", "200m", "400m", "Salto Largo", "Lanzamiento de Bala", "Gimnasia"
    };

    private Spinner spinnerDisciplina;
    private RecyclerView recycler;
    private View progressBar, layoutStats;
    private TextView tvVacio, tvMejorMarca, tvTotalRegistros, tvPrimeraMarca, tvUltimaMarca, tvTendencia;

    private final List<MarcaPersonal> lista = new ArrayList<>();
    private EvolucionMarcasAdapter adapter;

    private Long atletaId = null;
    private boolean spinnerReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evolucion_marcas);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String disciplinaInicial = getIntent().getStringExtra(EXTRA_DISCIPLINA);
        long atletaIdExtra = getIntent().getLongExtra(EXTRA_ATLETA_ID, -1L);
        if (atletaIdExtra != -1L) atletaId = atletaIdExtra;

        spinnerDisciplina = findViewById(R.id.spinnerDisciplina);
        recycler          = findViewById(R.id.recyclerEvolucion);
        progressBar       = findViewById(R.id.progressBar);
        layoutStats       = findViewById(R.id.layoutStats);
        tvVacio           = findViewById(R.id.tvVacio);
        tvMejorMarca      = findViewById(R.id.tvMejorMarca);
        tvTotalRegistros  = findViewById(R.id.tvTotalRegistros);
        tvPrimeraMarca    = findViewById(R.id.tvPrimeraMarca);
        tvUltimaMarca     = findViewById(R.id.tvUltimaMarca);
        tvTendencia       = findViewById(R.id.tvTendencia);

        adapter = new EvolucionMarcasAdapter(lista);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, DISCIPLINAS);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisciplina.setAdapter(adapterSpinner);

        // Pre-seleccionar disciplina si viene del intent
        int selIdx = 0;
        if (disciplinaInicial != null) {
            for (int i = 0; i < DISCIPLINAS.length; i++) {
                if (DISCIPLINAS[i].equals(disciplinaInicial)) { selIdx = i; break; }
            }
        }
        spinnerDisciplina.setSelection(selIdx, false);
        spinnerReady = true;

        spinnerDisciplina.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                if (spinnerReady) cargarMarcas(DISCIPLINAS[pos]);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        cargarMarcas(DISCIPLINAS[selIdx]);
    }

    private void cargarMarcas(String disciplina) {
        progressBar.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);
        layoutStats.setVisibility(View.GONE);
        recycler.setVisibility(View.GONE);

        Call<List<MarcaPersonal>> call = atletaId != null
                ? ApiClient.getMarcasService().getMarcasPorAtleta(atletaId, disciplina)
                : ApiClient.getMarcasService().getMarcas(disciplina);

        call.enqueue(new Callback<List<MarcaPersonal>>() {
            @Override
            public void onResponse(Call<List<MarcaPersonal>> c, Response<List<MarcaPersonal>> r) {
                progressBar.setVisibility(View.GONE);
                lista.clear();
                if (r.isSuccessful() && r.body() != null) {
                    // Ordenar por fecha ascendente (más antigua primero = timeline natural)
                    List<MarcaPersonal> ordenadas = new ArrayList<>(r.body());
                    ordenadas.sort((a, b) -> {
                        String fa = a.getFecha() != null ? a.getFecha() : "";
                        String fb = b.getFecha() != null ? b.getFecha() : "";
                        return fa.compareTo(fb);
                    });
                    lista.addAll(ordenadas);
                }

                if (lista.isEmpty()) {
                    tvVacio.setVisibility(View.VISIBLE);
                } else {
                    calcularStats();
                    recycler.setVisibility(View.VISIBLE);
                    layoutStats.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<MarcaPersonal>> c, Throwable t) {
                progressBar.setVisibility(View.GONE);
                tvVacio.setVisibility(View.VISIBLE);
            }
        });
    }

    private void calcularStats() {
        if (lista.isEmpty()) return;

        String unidad = lista.get(0).getUnidad() != null ? " " + lista.get(0).getUnidad() : "";

        // Mejor marca (PR)
        MarcaPersonal pr = null;
        for (MarcaPersonal m : lista) if (m.isEsMejorMarca()) { pr = m; break; }
        if (pr == null) pr = lista.get(lista.size() - 1); // fallback: última

        tvMejorMarca.setText((pr.getResultado() != null ? pr.getResultado() : "—") + unidad);
        tvTotalRegistros.setText(String.valueOf(lista.size()));

        // Primera y última
        MarcaPersonal primera = lista.get(0);
        MarcaPersonal ultima  = lista.get(lista.size() - 1);
        tvPrimeraMarca.setText((primera.getResultado() != null ? primera.getResultado() : "—") + unidad);
        tvUltimaMarca.setText((ultima.getResultado() != null ? ultima.getResultado() : "—") + unidad);

        // Tendencia: comparar primera vs última
        try {
            float vPrimera = Float.parseFloat(primera.getResultado().replace(",", "."));
            float vUltima  = Float.parseFloat(ultima.getResultado().replace(",", "."));
            if (Math.abs(vUltima - vPrimera) < 0.001f) {
                tvTendencia.setText("→");
                tvTendencia.setTextColor(getColor(R.color.colorTextSecondary));
            } else if (vUltima > vPrimera) {
                tvTendencia.setText("↑");
                tvTendencia.setTextColor(getColor(R.color.colorActive));
            } else {
                tvTendencia.setText("↓");
                tvTendencia.setTextColor(getColor(R.color.colorCancelled));
            }
        } catch (Exception e) {
            tvTendencia.setText("");
        }

        // Calcular máximo para la barra
        float max = 0f;
        for (MarcaPersonal m : lista) {
            try {
                float v = Float.parseFloat(m.getResultado().replace(",", "."));
                if (v > max) max = v;
            } catch (Exception ignored) {}
        }
        adapter.setValorMaximo(max);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
