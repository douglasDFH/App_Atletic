package com.example.tallerappmovil.asistencia;

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
import com.example.tallerappmovil.model.AsistenciaAtleta;
import com.example.tallerappmovil.model.AsistenciaRequest;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AsistenciaActivity extends AppCompatActivity {

    public static final String EXTRA_SESION_ID = "sesionId";
    public static final String EXTRA_GRUPO     = "grupoNombre";
    public static final String EXTRA_HORA      = "horaInicio";
    public static final String EXTRA_LUGAR     = "lugar";

    private AsistenciaAdapter adapter;
    private TextView tvResumen, tvVacio;
    private ProgressBar progressBar;
    private MaterialButton btnGuardar;
    private Long sesionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sesionId = getIntent().getLongExtra(EXTRA_SESION_ID, -1);
        String grupo = getIntent().getStringExtra(EXTRA_GRUPO);
        String hora  = getIntent().getStringExtra(EXTRA_HORA);
        String lugar = getIntent().getStringExtra(EXTRA_LUGAR);

        ((TextView) findViewById(R.id.tvSesionInfo))
                .setText(grupo != null ? grupo : "Sesión");

        String horaStr = formatHora(hora);
        String lugarStr = lugar != null ? "  ·  📍 " + lugar : "";
        ((TextView) findViewById(R.id.tvSesionDetalle))
                .setText("⏰ " + horaStr + lugarStr);

        tvResumen   = findViewById(R.id.tvResumen);
        tvVacio     = findViewById(R.id.tvVacio);
        progressBar = findViewById(R.id.progressBar);
        btnGuardar  = findViewById(R.id.btnGuardarAsistencia);

        RecyclerView recycler = findViewById(R.id.recyclerAtletas);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AsistenciaAdapter();
        adapter.setOnCambioListener(this::actualizarResumen);
        recycler.setAdapter(adapter);

        btnGuardar.setOnClickListener(v -> guardar());

        cargarAtletas();
    }

    private void cargarAtletas() {
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getAsistenciaService()
                .getAsistencia(sesionId)
                .enqueue(new Callback<List<AsistenciaAtleta>>() {
                    @Override
                    public void onResponse(Call<List<AsistenciaAtleta>> call,
                                           Response<List<AsistenciaAtleta>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            List<AsistenciaAtleta> lista = response.body();
                            adapter.setAtletas(lista);
                            tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
                            actualizarResumen();
                        } else {
                            tvVacio.setVisibility(View.VISIBLE);
                            Toast.makeText(AsistenciaActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AsistenciaAtleta>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvVacio.setVisibility(View.VISIBLE);
                        Toast.makeText(AsistenciaActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void actualizarResumen() {
        Map<Long, String> estados = adapter.getEstados();
        int presentes = 0;
        for (String e : estados.values()) {
            if ("PRESENTE".equals(e)) presentes++;
        }
        int total = adapter.getItemCount();
        int pct   = total > 0 ? (presentes * 100 / total) : 0;
        tvResumen.setText(presentes + "/" + total + " presentes (" + pct + "%)");
    }

    private void guardar() {
        if (!adapter.todosSeleccionados()) {
            Toast.makeText(this, getString(R.string.err_asistencia_incompleta),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        List<AsistenciaRequest> lista = new ArrayList<>();
        for (Map.Entry<Long, String> entry : adapter.getEstados().entrySet()) {
            lista.add(new AsistenciaRequest(entry.getKey(), entry.getValue()));
        }

        btnGuardar.setEnabled(false);
        ApiClient.getAsistenciaService()
                .guardarAsistencia(sesionId, lista)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        btnGuardar.setEnabled(true);
                        if (response.isSuccessful()) {
                            Toast.makeText(AsistenciaActivity.this,
                                    getString(R.string.msg_asistencia_guardada),
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AsistenciaActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        btnGuardar.setEnabled(true);
                        Toast.makeText(AsistenciaActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String formatHora(String iso) {
        if (iso == null) return "--";
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(iso);
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(d);
        } catch (Exception e) {
            return iso;
        }
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
