package com.example.tallerappmovil.marcas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.Disciplina;
import com.example.tallerappmovil.model.GrupoEvolucionDto;
import com.example.tallerappmovil.model.MarcaPersonal;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EvolucionGrupoActivity extends AppCompatActivity {

    public static final String EXTRA_GRUPO_ID     = "grupoId";
    public static final String EXTRA_GRUPO_NOMBRE = "grupoNombre";
    public static final String EXTRA_DISCIPLINA   = "disciplina";

    private static final int[] COLORES = {
            0xFF26C6DA, 0xFFFF7043, 0xFF66BB6A, 0xFFAB47BC,
            0xFFFFCA28, 0xFF42A5F5, 0xFFEF5350, 0xFF26A69A
    };

    private static final SimpleDateFormat ISO   = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat LABEL = new SimpleDateFormat("dd/MM", Locale.getDefault());

    private Long grupoId;
    private String grupoNombre;
    private AutoCompleteTextView spinnerDisciplina;
    private LineChart lineChart;
    private ProgressBar progressBar;
    private View scrollContent;
    private TextView tvVacio;
    private RecyclerView recyclerLeyenda;
    private LeyendaAtletaAdapter leyendaAdapter;
    private List<GrupoEvolucionDto> datosActuales = new ArrayList<>();
    private final List<Disciplina> disciplinasList = new ArrayList<>();
    private boolean spinnerReady = false;
    private final List<String> etiquetasX = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evolucion_grupo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        grupoId     = getIntent().getLongExtra(EXTRA_GRUPO_ID, -1L);
        grupoNombre = getIntent().getStringExtra(EXTRA_GRUPO_NOMBRE);
        String disciplinaInicial = getIntent().getStringExtra(EXTRA_DISCIPLINA);

        if (grupoNombre != null && getSupportActionBar() != null)
            getSupportActionBar().setTitle(grupoNombre);

        spinnerDisciplina = findViewById(R.id.spinnerDisciplina);
        lineChart         = findViewById(R.id.lineChart);
        progressBar       = findViewById(R.id.progressBar);
        scrollContent     = findViewById(R.id.scrollContent);
        tvVacio           = findViewById(R.id.tvVacio);
        recyclerLeyenda   = findViewById(R.id.recyclerLeyenda);

        leyendaAdapter = new LeyendaAtletaAdapter();
        recyclerLeyenda.setLayoutManager(new LinearLayoutManager(this));
        recyclerLeyenda.setAdapter(leyendaAdapter);

        configurarGrafica();
        cargarDisciplinas(disciplinaInicial);
    }

    private void cargarDisciplinas(String disciplinaInicial) {
        ApiClient.getDisciplinasService().listar().enqueue(new Callback<List<Disciplina>>() {
            @Override
            public void onResponse(Call<List<Disciplina>> c, Response<List<Disciplina>> r) {
                if (r.isSuccessful() && r.body() != null) {
                    disciplinasList.clear();
                    disciplinasList.addAll(r.body());
                    List<String> nombres = new ArrayList<>();
                    for (Disciplina d : disciplinasList) nombres.add(d.getNombre());

                    ArrayAdapter<String> discAdapter = new ArrayAdapter<>(
                            EvolucionGrupoActivity.this,
                            android.R.layout.simple_dropdown_item_1line, nombres);
                    spinnerDisciplina.setAdapter(discAdapter);

                    int selIdx = 0;
                    if (disciplinaInicial != null) {
                        for (int i = 0; i < nombres.size(); i++) {
                            if (nombres.get(i).equals(disciplinaInicial)) { selIdx = i; break; }
                        }
                    }
                    final String selNombre = disciplinasList.isEmpty()
                            ? "" : disciplinasList.get(selIdx).getNombre();
                    spinnerDisciplina.setText(selNombre, false);
                    spinnerReady = true;

                    spinnerDisciplina.setOnItemClickListener((parent, view, position, id) -> {
                        if (spinnerReady && position < disciplinasList.size())
                            cargarDatos(disciplinasList.get(position).getNombre());
                    });

                    if (!disciplinasList.isEmpty()) cargarDatos(selNombre);
                }
            }
            @Override
            public void onFailure(Call<List<Disciplina>> c, Throwable t) {
                Toast.makeText(EvolucionGrupoActivity.this,
                        "No se pudieron cargar las disciplinas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarGrafica() {
        int colorText   = getResources().getColor(R.color.colorTextSecondary, getTheme());
        int colorBorder = getResources().getColor(R.color.colorBorder, getTheme());

        lineChart.setBackgroundColor(Color.TRANSPARENT);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setNoDataText(getString(R.string.msg_sin_datos_grupo));
        lineChart.setNoDataTextColor(colorText);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(colorText);
        xAxis.setTextSize(9f);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int i = (int) value;
                return (i >= 0 && i < etiquetasX.size()) ? etiquetasX.get(i) : "";
            }
        });

        lineChart.getAxisLeft().setTextColor(colorText);
        lineChart.getAxisLeft().setTextSize(9f);
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setGridColor(colorBorder);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.getAxisRight().setEnabled(false);
    }

    private void cargarDatos(String disciplina) {
        progressBar.setVisibility(View.VISIBLE);
        scrollContent.setVisibility(View.GONE);
        tvVacio.setVisibility(View.GONE);

        ApiClient.getMarcasService().getMarcasGrupo(grupoId, disciplina)
                .enqueue(new Callback<List<GrupoEvolucionDto>>() {
                    @Override
                    public void onResponse(Call<List<GrupoEvolucionDto>> call,
                                           Response<List<GrupoEvolucionDto>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null
                                && !response.body().isEmpty()) {
                            datosActuales = response.body();
                            poblarGrafica(datosActuales);
                            scrollContent.setVisibility(View.VISIBLE);
                        } else {
                            datosActuales = new ArrayList<>();
                            lineChart.clear();
                            leyendaAdapter.setItems(new ArrayList<>(), new int[0]);
                            tvVacio.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<GrupoEvolucionDto>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvVacio.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void poblarGrafica(List<GrupoEvolucionDto> datos) {
        etiquetasX.clear();
        List<ILineDataSet> dataSets = new ArrayList<>();
        List<LeyendaAtletaAdapter.Item> leyendaItems = new ArrayList<>();
        int[] coloresUsados = new int[datos.size()];

        // Construir eje X unificado con todas las fechas únicas ordenadas
        List<String> todasFechas = new ArrayList<>();
        for (GrupoEvolucionDto atleta : datos) {
            if (atleta.getMarcas() == null) continue;
            for (MarcaPersonal m : atleta.getMarcas()) {
                if (m.getFecha() != null && !todasFechas.contains(m.getFecha()))
                    todasFechas.add(m.getFecha());
            }
        }
        todasFechas.sort(String::compareTo);

        for (String f : todasFechas) {
            String label = f;
            if (f.length() >= 10) {
                try {
                    Date d = ISO.parse(f);
                    if (d != null) label = LABEL.format(d);
                } catch (ParseException ignored) {
                    label = f.substring(5, 10).replace("-", "/");
                }
            }
            etiquetasX.add(label);
        }

        for (int i = 0; i < datos.size(); i++) {
            GrupoEvolucionDto atleta = datos.get(i);
            int color = COLORES[i % COLORES.length];
            coloresUsados[i] = color;

            if (atleta.getMarcas() == null || atleta.getMarcas().isEmpty()) continue;

            List<Entry> entries = new ArrayList<>();
            for (MarcaPersonal m : atleta.getMarcas()) {
                int xIdx = todasFechas.indexOf(m.getFecha());
                if (xIdx < 0) continue;
                try {
                    float valor = Float.parseFloat(m.getResultado().replace(",", "."));
                    entries.add(new Entry(xIdx, valor));
                } catch (Exception ignored) {}
            }

            if (entries.isEmpty()) continue;

            LineDataSet ds = new LineDataSet(entries, atleta.getAtletaNombre());
            ds.setColor(color);
            ds.setCircleColor(color);
            ds.setCircleHoleColor(getResources().getColor(R.color.colorSurface, getTheme()));
            ds.setCircleRadius(4f);
            ds.setCircleHoleRadius(2f);
            ds.setLineWidth(2f);
            ds.setDrawValues(false);
            ds.setMode(LineDataSet.Mode.LINEAR);
            dataSets.add(ds);

            // Mejor marca del atleta
            String mejor = "—";
            String unidad = "";
            for (MarcaPersonal m : atleta.getMarcas()) {
                if (m.isEsMejorMarca()) {
                    mejor = m.getResultado();
                    unidad = m.getUnidad() != null ? " " + m.getUnidad() : "";
                    break;
                }
            }
            leyendaItems.add(new LeyendaAtletaAdapter.Item(
                    atleta.getAtletaNombre(), mejor + unidad, color));
        }

        if (dataSets.isEmpty()) {
            lineChart.clear();
            return;
        }

        lineChart.setData(new LineData(dataSets));
        lineChart.getXAxis().setLabelCount(Math.min(etiquetasX.size(), 6), false);
        lineChart.animateX(500);
        lineChart.invalidate();
        leyendaAdapter.setItems(leyendaItems, coloresUsados);
    }

    // ── PDF Export ─────────────────────────────────────────────────────────
    private void exportarPdf() {
        if (datosActuales.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_sin_datos_grupo), Toast.LENGTH_SHORT).show();
            return;
        }

        String disciplina = spinnerDisciplina.getText().toString();
        Bitmap chartBitmap = lineChart.getChartBitmap();

        PdfDocument doc = new PdfDocument();
        int pageW = 595, pageH = 842; // A4 pts
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageW, pageH, 1).create();
        PdfDocument.Page page = doc.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paintTitle = new Paint();
        paintTitle.setColor(Color.parseColor("#0D1B2A"));
        paintTitle.setTextSize(16f);
        paintTitle.setTypeface(Typeface.DEFAULT_BOLD);

        Paint paintSub = new Paint();
        paintSub.setColor(Color.parseColor("#546E7A"));
        paintSub.setTextSize(11f);

        Paint paintRow = new Paint();
        paintRow.setTextSize(11f);

        int y = 40;
        canvas.drawText("Evolución del Grupo — " + (grupoNombre != null ? grupoNombre : ""), 24, y, paintTitle);
        y += 20;
        canvas.drawText("Disciplina: " + disciplina, 24, y, paintSub);
        y += 30;

        // Gráfica
        int chartH = 220;
        Bitmap scaled = Bitmap.createScaledBitmap(chartBitmap, pageW - 48, chartH, true);
        canvas.drawBitmap(scaled, 24, y, null);
        y += chartH + 20;

        // Tabla leyenda
        canvas.drawText("Atleta", 24, y, paintSub);
        canvas.drawText("Mejor marca", 380, y, paintSub);
        y += 16;

        Paint lineP = new Paint();
        lineP.setColor(Color.LTGRAY);
        lineP.setStrokeWidth(0.5f);
        canvas.drawLine(24, y, pageW - 24, y, lineP);
        y += 12;

        for (int i = 0; i < datosActuales.size(); i++) {
            GrupoEvolucionDto atleta = datosActuales.get(i);
            int color = COLORES[i % COLORES.length];
            paintRow.setColor(color);
            canvas.drawRect(24, y - 9, 30, y + 1, paintRow);

            paintRow.setColor(Color.parseColor("#212121"));
            canvas.drawText(atleta.getAtletaNombre(), 36, y, paintRow);

            String mejor = "—";
            String unidad = "";
            if (atleta.getMarcas() != null) {
                for (MarcaPersonal m : atleta.getMarcas()) {
                    if (m.isEsMejorMarca()) {
                        mejor = m.getResultado();
                        unidad = m.getUnidad() != null ? " " + m.getUnidad() : "";
                        break;
                    }
                }
            }
            paintRow.setColor(Color.parseColor("#00796B"));
            canvas.drawText(mejor + unidad, 380, y, paintRow);
            y += 20;
        }

        doc.finishPage(page);

        try {
            File cacheDir = getExternalCacheDir();
            if (cacheDir == null) cacheDir = getCacheDir();
            File pdfFile = new File(cacheDir, "evolucion_grupo.pdf");
            doc.writeTo(new FileOutputStream(pdfFile));
            doc.close();

            Uri uri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", pdfFile);

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(share, getString(R.string.lbl_exportar_pdf)));
        } catch (Exception e) {
            Toast.makeText(this, "Error al generar PDF", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_evolucion_grupo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        if (item.getItemId() == R.id.action_exportar_pdf) { exportarPdf(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
