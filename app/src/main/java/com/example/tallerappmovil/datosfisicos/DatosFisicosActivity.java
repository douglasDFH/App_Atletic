package com.example.tallerappmovil.datosfisicos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.DatosFisicos;
import com.example.tallerappmovil.session.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatosFisicosActivity extends AppCompatActivity {

    public static final String EXTRA_ATLETA_ID     = "atletaId";
    public static final String EXTRA_ATLETA_NOMBRE = "atletaNombre";

    private DatosFisicosAdapter adapter;
    private ProgressBar progressBar;
    private View scrollContent;
    private TextView tvVacio;
    private TextView tvResumenImc, tvResumenPeso, tvResumenAltura, tvResumenFecha;
    private View cardResumen;

    private Long atletaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_fisicos);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        atletaId = getIntent().getLongExtra(EXTRA_ATLETA_ID, -1L);
        String atletaNombre = getIntent().getStringExtra(EXTRA_ATLETA_NOMBRE);
        if (atletaNombre != null && getSupportActionBar() != null)
            getSupportActionBar().setTitle(atletaNombre);

        progressBar    = findViewById(R.id.progressBar);
        scrollContent  = findViewById(R.id.scrollContent);
        tvVacio        = findViewById(R.id.tvVacio);
        cardResumen    = findViewById(R.id.cardResumen);
        tvResumenImc   = findViewById(R.id.tvResumenImc);
        tvResumenPeso  = findViewById(R.id.tvResumenPeso);
        tvResumenAltura = findViewById(R.id.tvResumenAltura);
        tvResumenFecha = findViewById(R.id.tvResumenFecha);

        RecyclerView recycler = findViewById(R.id.recyclerDatosFisicos);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DatosFisicosAdapter();
        recycler.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabRegistrar);
        SessionManager session = new SessionManager(this);
        String rol = session.getUserRole();

        if ("ENTRENADOR".equals(rol) || "ADMIN".equals(rol)) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(this, RegistrarDatosFisicosActivity.class);
                intent.putExtra(EXTRA_ATLETA_ID, atletaId);
                intent.putExtra(EXTRA_ATLETA_NOMBRE, atletaNombre);
                startActivity(intent);
            });
        }

        if (atletaId != -1L) cargarHistorial();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (atletaId != -1L) cargarHistorial();
    }

    private void cargarHistorial() {
        progressBar.setVisibility(View.VISIBLE);
        scrollContent.setVisibility(View.GONE);
        tvVacio.setVisibility(View.GONE);

        ApiClient.getDatosFisicosService().historial(atletaId)
                .enqueue(new Callback<List<DatosFisicos>>() {
                    @Override
                    public void onResponse(Call<List<DatosFisicos>> c, Response<List<DatosFisicos>> r) {
                        progressBar.setVisibility(View.GONE);
                        if (r.isSuccessful() && r.body() != null && !r.body().isEmpty()) {
                            List<DatosFisicos> lista = r.body();
                            adapter.setLista(lista);
                            mostrarResumen(lista.get(0));
                            scrollContent.setVisibility(View.VISIBLE);
                        } else {
                            tvVacio.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<DatosFisicos>> c, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvVacio.setVisibility(View.VISIBLE);
                        Toast.makeText(DatosFisicosActivity.this,
                                "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarResumen(DatosFisicos d) {
        cardResumen.setVisibility(View.VISIBLE);
        tvResumenImc.setText(d.getImc() != null
                ? String.format(Locale.getDefault(), "%.1f", d.getImc()) : "—");
        tvResumenPeso.setText(d.getPesoKg() != null
                ? String.format(Locale.getDefault(), "%.1f", d.getPesoKg()) : "—");
        tvResumenAltura.setText(d.getAlturaCm() != null
                ? String.format(Locale.getDefault(), "%.0f", d.getAlturaCm()) : "—");
        tvResumenFecha.setText("Medición del " + formatFecha(d.getFecha()));
    }

    private String formatFecha(String fecha) {
        if (fecha == null || fecha.length() < 10) return "";
        try {
            String[] p = fecha.substring(0, 10).split("-");
            return p[2] + "/" + p[1] + "/" + p[0];
        } catch (Exception e) { return fecha; }
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
