package com.example.tallerappmovil.estadisticas;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.Competencia;
import com.example.tallerappmovil.model.AtletaInfo;
import com.example.tallerappmovil.model.ReporteAtleta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstadisticasActivity extends AppCompatActivity {

    private View scrollContent;
    private ProgressBar progressBar;
    private TextView tvMesActual;
    private TextView tvTotalAtletas;
    private TextView tvProxCompetencias;
    private TextView tvPromedioAsistencia;
    private TextView tvResumenSesiones;
    private TextView tvPresentes;
    private TextView tvAusentes;
    private RecyclerView recyclerRanking;
    private TextView tvSinReporte;
    private View layoutProxEventos;
    private TextView tvSinEventos;

    private RankingAsistenciaAdapter rankingAdapter;

    private int pendingCalls = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scrollContent         = findViewById(R.id.scrollContent);
        progressBar           = findViewById(R.id.progressBar);
        tvMesActual           = findViewById(R.id.tvMesActual);
        tvTotalAtletas        = findViewById(R.id.tvTotalAtletas);
        tvProxCompetencias    = findViewById(R.id.tvProxCompetencias);
        tvPromedioAsistencia  = findViewById(R.id.tvPromedioAsistencia);
        tvResumenSesiones     = findViewById(R.id.tvResumenSesiones);
        tvPresentes           = findViewById(R.id.tvPresentes);
        tvAusentes            = findViewById(R.id.tvAusentes);
        tvSinReporte          = findViewById(R.id.tvSinReporte);
        tvSinEventos          = findViewById(R.id.tvSinEventos);
        layoutProxEventos     = findViewById(R.id.layoutProxEventos);

        recyclerRanking = findViewById(R.id.recyclerRanking);
        recyclerRanking.setLayoutManager(new LinearLayoutManager(this));
        rankingAdapter = new RankingAsistenciaAdapter();
        recyclerRanking.setAdapter(rankingAdapter);

        Calendar cal = Calendar.getInstance();
        String mesDisplay = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"))
                .format(cal.getTime());
        mesDisplay = Character.toUpperCase(mesDisplay.charAt(0)) + mesDisplay.substring(1);
        tvMesActual.setText(mesDisplay);

        String mesApi = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());

        cargarDatos(mesApi);
    }

    private void cargarDatos(String mes) {
        progressBar.setVisibility(View.VISIBLE);
        scrollContent.setVisibility(View.GONE);
        pendingCalls = 3;

        // 1. Total atletas
        ApiClient.getAtletasService().getAtletas()
                .enqueue(new Callback<List<AtletaInfo>>() {
                    @Override
                    public void onResponse(Call<List<AtletaInfo>> call,
                                           Response<List<AtletaInfo>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            tvTotalAtletas.setText(String.valueOf(response.body().size()));
                        } else {
                            tvTotalAtletas.setText("--");
                        }
                        checkAllLoaded();
                    }
                    @Override
                    public void onFailure(Call<List<AtletaInfo>> call, Throwable t) {
                        tvTotalAtletas.setText("--");
                        checkAllLoaded();
                    }
                });

        // 2. Próximas competencias
        ApiClient.getCompetenciasService().getCompetencias("PROXIMO")
                .enqueue(new Callback<List<Competencia>>() {
                    @Override
                    public void onResponse(Call<List<Competencia>> call,
                                           Response<List<Competencia>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Competencia> proximas = response.body();
                            tvProxCompetencias.setText(String.valueOf(proximas.size()));
                            mostrarProximasCompetencias(proximas);
                        } else {
                            tvProxCompetencias.setText("0");
                            tvSinEventos.setVisibility(View.VISIBLE);
                        }
                        checkAllLoaded();
                    }
                    @Override
                    public void onFailure(Call<List<Competencia>> call, Throwable t) {
                        tvProxCompetencias.setText("--");
                        tvSinEventos.setVisibility(View.VISIBLE);
                        checkAllLoaded();
                    }
                });

        // 3. Reporte de asistencia del mes
        ApiClient.getAsistenciaService().getReporte(null, mes)
                .enqueue(new Callback<List<ReporteAtleta>>() {
                    @Override
                    public void onResponse(Call<List<ReporteAtleta>> call,
                                           Response<List<ReporteAtleta>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && !response.body().isEmpty()) {
                            mostrarReporte(response.body());
                        } else {
                            tvSinReporte.setVisibility(View.VISIBLE);
                            tvPromedioAsistencia.setText("--%");
                        }
                        checkAllLoaded();
                    }
                    @Override
                    public void onFailure(Call<List<ReporteAtleta>> call, Throwable t) {
                        tvSinReporte.setVisibility(View.VISIBLE);
                        tvPromedioAsistencia.setText("--%");
                        checkAllLoaded();
                    }
                });
    }

    private void mostrarReporte(List<ReporteAtleta> lista) {
        // Calcular totales globales
        int totalPresentes = 0, totalAusentes = 0, totalSesiones = 0;
        for (ReporteAtleta r : lista) {
            totalPresentes += r.getPresentes();
            totalAusentes  += r.getAusentes();
            totalSesiones  += r.getTotalSesiones();
        }

        // Promedio ponderado de asistencia
        float promedio = totalSesiones > 0
                ? (totalPresentes * 100f) / totalSesiones : 0f;
        int pctInt = Math.round(promedio);

        tvPromedioAsistencia.setText(pctInt + "%");

        // Color según el promedio
        int colorPct;
        if (pctInt >= 80) colorPct = getColor(R.color.colorActive);
        else if (pctInt >= 50) colorPct = getColor(R.color.colorPending);
        else colorPct = getColor(R.color.colorCancelled);
        tvPromedioAsistencia.setTextColor(colorPct);

        tvPresentes.setText(String.valueOf(totalPresentes));
        tvPresentes.setTextColor(getColor(R.color.colorActive));
        tvAusentes.setText(String.valueOf(totalAusentes));
        tvAusentes.setTextColor(getColor(R.color.colorCancelled));

        tvResumenSesiones.setText(lista.size() + " atletas · " + totalSesiones + " registros");

        // Ordenar por porcentaje desc para el ranking
        List<ReporteAtleta> ordenados = new ArrayList<>(lista);
        Collections.sort(ordenados, (a, b) -> {
            float pctA = calcPct(a);
            float pctB = calcPct(b);
            return Float.compare(pctB, pctA); // desc
        });

        // Mostrar top 10 en el ranking
        List<ReporteAtleta> top = ordenados.size() > 10
                ? ordenados.subList(0, 10) : ordenados;
        rankingAdapter.setLista(top);
    }

    private float calcPct(ReporteAtleta r) {
        if (r.getPorcentajeAsistencia() > 0f) return r.getPorcentajeAsistencia();
        return r.getTotalSesiones() > 0 ? (r.getPresentes() * 100f) / r.getTotalSesiones() : 0f;
    }

    private void mostrarProximasCompetencias(List<Competencia> proximas) {
        if (proximas.isEmpty()) {
            tvSinEventos.setVisibility(View.VISIBLE);
            return;
        }
        // Mostrar máximo 4 próximas
        int limit = Math.min(proximas.size(), 4);
        SimpleDateFormat isoFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dispFmt = new SimpleDateFormat("dd MMM", new Locale("es"));
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < limit; i++) {
            Competencia c = proximas.get(i);
            View item = inflater.inflate(R.layout.item_evento_mini, (android.view.ViewGroup) layoutProxEventos, false);

            TextView tvFecha   = item.findViewById(R.id.tvFechaMini);
            TextView tvNombre  = item.findViewById(R.id.tvNombreMini);
            TextView tvCategoria = item.findViewById(R.id.tvCategoriaMini);

            if (c.getFechaEvento() != null) {
                try {
                    Date d = isoFmt.parse(c.getFechaEvento());
                    tvFecha.setText(d != null ? dispFmt.format(d).toUpperCase(Locale.getDefault()) : c.getFechaEvento());
                } catch (Exception e) {
                    tvFecha.setText(c.getFechaEvento());
                }
            }
            tvNombre.setText(c.getNombre() != null ? c.getNombre() : "");
            if (tvCategoria != null) {
                tvCategoria.setText(c.getCategoria() != null ? c.getCategoria() : "");
            }

            // Separador entre items (no en el último)
            if (i < limit - 1) {
                View divider = new View(this);
                android.widget.LinearLayout.LayoutParams lp =
                        new android.widget.LinearLayout.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1);
                lp.setMarginStart(16);
                lp.setMarginEnd(16);
                divider.setLayoutParams(lp);
                divider.setBackgroundColor(getColor(R.color.colorBorder));
                ((android.widget.LinearLayout) layoutProxEventos).addView(divider);
            }

            ((android.widget.LinearLayout) layoutProxEventos).addView(item);
        }
    }

    private void checkAllLoaded() {
        pendingCalls--;
        if (pendingCalls <= 0) {
            progressBar.setVisibility(View.GONE);
            scrollContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
