package com.example.tallerappmovil.marcas;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.MarcaPersonal;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EvolucionMarcasActivity extends AppCompatActivity {

    public static final String EXTRA_DISCIPLINA = "disciplina";
    public static final String EXTRA_ATLETA_ID  = "atleta_id";

    private static final String[] DISCIPLINAS = {
            "100m", "200m", "400m", "Salto Largo", "Lanzamiento de Bala", "Gimnasia"
    };

    private static final SimpleDateFormat ISO   = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat LABEL = new SimpleDateFormat("dd/MM", Locale.getDefault());

    private Spinner spinnerDisciplina;
    private RecyclerView recycler;
    private View progressBar, scrollContent, layoutStats;
    private TextView tvVacio, tvMejorMarca, tvTotalRegistros, tvPrimeraMarca, tvUltimaMarca, tvTendencia;
    private LineChart lineChart;

    private final List<MarcaPersonal> lista = new ArrayList<>();
    private EvolucionMarcasAdapter adapter;
    private Long atletaId = null;
    private boolean spinnerReady = false;

    // Etiquetas de fecha para el eje X de la gráfica
    private final List<String> etiquetasX = new ArrayList<>();

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
        scrollContent     = findViewById(R.id.scrollContent);
        tvVacio           = findViewById(R.id.tvVacio);
        tvMejorMarca      = findViewById(R.id.tvMejorMarca);
        tvTotalRegistros  = findViewById(R.id.tvTotalRegistros);
        tvPrimeraMarca    = findViewById(R.id.tvPrimeraMarca);
        tvUltimaMarca     = findViewById(R.id.tvUltimaMarca);
        tvTendencia       = findViewById(R.id.tvTendencia);
        layoutStats       = findViewById(R.id.layoutStats);
        lineChart         = findViewById(R.id.lineChart);

        adapter = new EvolucionMarcasAdapter(lista);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        configurarGrafica();

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, DISCIPLINAS);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisciplina.setAdapter(adapterSpinner);

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

    private void configurarGrafica() {
        int colorText    = ContextCompat.getColor(this, R.color.colorTextSecondary);
        int colorBorder  = ContextCompat.getColor(this, R.color.colorBorder);

        lineChart.setBackgroundColor(Color.TRANSPARENT);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);
        lineChart.setNoDataText("Sin datos");
        lineChart.setNoDataTextColor(colorText);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(colorText);
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int idx = (int) value;
                if (idx >= 0 && idx < etiquetasX.size()) return etiquetasX.get(idx);
                return "";
            }
        });

        YAxis left = lineChart.getAxisLeft();
        left.setTextColor(colorText);
        left.setTextSize(10f);
        left.setDrawGridLines(true);
        left.setGridColor(colorBorder);
        left.setDrawAxisLine(false);

        lineChart.getAxisRight().setEnabled(false);
    }

    private void cargarMarcas(String disciplina) {
        progressBar.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);
        scrollContent.setVisibility(View.GONE);
        layoutStats.setVisibility(View.GONE);

        Call<List<MarcaPersonal>> call = atletaId != null
                ? ApiClient.getMarcasService().getMarcasPorAtleta(atletaId, disciplina)
                : ApiClient.getMarcasService().getMarcas(disciplina);

        call.enqueue(new Callback<List<MarcaPersonal>>() {
            @Override
            public void onResponse(Call<List<MarcaPersonal>> c, Response<List<MarcaPersonal>> r) {
                progressBar.setVisibility(View.GONE);
                lista.clear();
                if (r.isSuccessful() && r.body() != null) {
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
                    lineChart.clear();
                } else {
                    calcularStats();
                    poblarGrafica();
                    layoutStats.setVisibility(View.VISIBLE);
                    scrollContent.setVisibility(View.VISIBLE);
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

    private void poblarGrafica() {
        etiquetasX.clear();
        List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < lista.size(); i++) {
            MarcaPersonal m = lista.get(i);
            try {
                float valor = Float.parseFloat(m.getResultado().replace(",", "."));
                entries.add(new Entry(i, valor));
            } catch (Exception ignored) {}

            // Etiqueta del eje X: dd/MM
            String fechaLabel = "";
            if (m.getFecha() != null && m.getFecha().length() >= 10) {
                try {
                    Date d = ISO.parse(m.getFecha());
                    if (d != null) fechaLabel = LABEL.format(d);
                } catch (ParseException e) {
                    fechaLabel = m.getFecha().substring(5, 10).replace("-", "/");
                }
            }
            etiquetasX.add(fechaLabel);
        }

        if (entries.isEmpty()) {
            lineChart.clear();
            return;
        }

        int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);

        LineDataSet dataSet = new LineDataSet(entries, "Marca");
        dataSet.setColor(colorPrimary);
        dataSet.setCircleColor(colorPrimary);
        dataSet.setCircleHoleColor(ContextCompat.getColor(this, R.color.colorSurface));
        dataSet.setCircleRadius(5f);
        dataSet.setCircleHoleRadius(2.5f);
        dataSet.setLineWidth(2.5f);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(9f);
        dataSet.setValueTextColor(colorPrimary);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(colorPrimary);
        dataSet.setFillAlpha(30);

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String unidad = lista.isEmpty() || lista.get(0).getUnidad() == null
                        ? "" : " " + lista.get(0).getUnidad();
                return value % 1 == 0
                        ? String.valueOf((int) value) + unidad
                        : String.format(Locale.getDefault(), "%.2f", value) + unidad;
            }
        });

        lineChart.setData(new LineData(dataSet));
        lineChart.getXAxis().setLabelCount(Math.min(entries.size(), 6), false);
        lineChart.animateX(600);
        lineChart.invalidate();
    }

    private void calcularStats() {
        if (lista.isEmpty()) return;

        String unidad = lista.get(0).getUnidad() != null ? " " + lista.get(0).getUnidad() : "";

        MarcaPersonal pr = null;
        for (MarcaPersonal m : lista) if (m.isEsMejorMarca()) { pr = m; break; }
        if (pr == null) pr = lista.get(lista.size() - 1);

        tvMejorMarca.setText((pr.getResultado() != null ? pr.getResultado() : "—") + unidad);
        tvTotalRegistros.setText(String.valueOf(lista.size()));

        MarcaPersonal primera = lista.get(0);
        MarcaPersonal ultima  = lista.get(lista.size() - 1);
        tvPrimeraMarca.setText((primera.getResultado() != null ? primera.getResultado() : "—") + unidad);
        tvUltimaMarca.setText((ultima.getResultado() != null ? ultima.getResultado() : "—") + unidad);

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
