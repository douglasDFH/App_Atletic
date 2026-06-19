package com.example.tallerappmovil.eventos;

import android.content.res.ColorStateList;
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
import com.example.tallerappmovil.model.AtletaInfo;
import com.example.tallerappmovil.model.Competencia;
import com.example.tallerappmovil.session.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompetenciaDetalleActivity extends AppCompatActivity {

    public static final String EXTRA_ID     = "competenciaId";
    public static final String EXTRA_NOMBRE = "competenciaNombre";

    private static final SimpleDateFormat ISO  =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat FULL =
            new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es"));

    private Long competenciaId;
    private boolean esAtleta;
    private boolean inscritoActual = false;

    private TextView tvNombre, tvEstado, tvDisciplina, tvFecha, tvCategoria, tvLugar;
    private TextView tvDescripcion, tvHeaderInscritos, tvSinInscritos;
    private View layoutDescripcion;
    private MaterialButton btnInscripcion;
    private ProgressBar progressBar;
    private View scrollContent;
    private InscritosAdapter inscritosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competencia_detalle);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        competenciaId = getIntent().getLongExtra(EXTRA_ID, -1L);
        String nombre = getIntent().getStringExtra(EXTRA_NOMBRE);
        if (nombre != null && getSupportActionBar() != null)
            getSupportActionBar().setTitle(nombre);

        SessionManager session = new SessionManager(this);
        String rol = session.getUserRole();
        esAtleta = "ATLETA".equals(rol);

        tvNombre          = findViewById(R.id.tvNombreDetalle);
        tvEstado          = findViewById(R.id.tvEstadoDetalle);
        tvDisciplina      = findViewById(R.id.tvDisciplinaDetalle);
        tvFecha           = findViewById(R.id.tvFechaDetalle);
        tvCategoria       = findViewById(R.id.tvCategoriaDetalle);
        tvLugar           = findViewById(R.id.tvLugarDetalle);
        tvDescripcion     = findViewById(R.id.tvDescripcionDetalle);
        layoutDescripcion = findViewById(R.id.layoutDescripcion);
        btnInscripcion    = findViewById(R.id.btnInscripcion);
        tvHeaderInscritos = findViewById(R.id.tvHeaderInscritos);
        tvSinInscritos    = findViewById(R.id.tvSinInscritos);
        progressBar       = findViewById(R.id.progressBar);
        scrollContent     = findViewById(R.id.scrollContent);

        RecyclerView recycler = findViewById(R.id.recyclerInscritos);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        inscritosAdapter = new InscritosAdapter();
        recycler.setAdapter(inscritosAdapter);

        if (competenciaId != -1L) cargarDetalle();
    }

    private void cargarDetalle() {
        progressBar.setVisibility(View.VISIBLE);

        ApiClient.getCompetenciasService().getCompetencia(competenciaId)
                .enqueue(new Callback<Competencia>() {
                    @Override
                    public void onResponse(Call<Competencia> call, Response<Competencia> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            mostrarDetalle(response.body());
                            scrollContent.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(CompetenciaDetalleActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Competencia> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CompetenciaDetalleActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarDetalle(Competencia c) {
        tvNombre.setText(c.getNombre());
        tvDisciplina.setText(c.getDisciplina() != null ? c.getDisciplina() : "");
        tvCategoria.setText(c.getCategoria() != null ? c.getCategoria() : "--");
        tvLugar.setText(c.getLugar() != null ? c.getLugar() : "--");

        // Fecha formateada
        try {
            Date d = ISO.parse(c.getFechaEvento());
            tvFecha.setText(d != null ? FULL.format(d) : c.getFechaEvento());
        } catch (Exception e) {
            tvFecha.setText(c.getFechaEvento() != null ? c.getFechaEvento() : "--");
        }

        // Estado badge
        String estado = c.getEstado() != null ? c.getEstado() : "";
        tvEstado.setText(estadoLabel(estado));
        tvEstado.setBackgroundTintList(
                ColorStateList.valueOf(estadoColor(estado)));
        tvEstado.setVisibility(View.VISIBLE);

        // Descripción
        if (c.getDescripcion() != null && !c.getDescripcion().isEmpty()) {
            tvDescripcion.setText(c.getDescripcion());
            layoutDescripcion.setVisibility(View.VISIBLE);
        }

        // Botón inscripción (solo atleta, solo eventos próximos o en curso)
        boolean puedeInscribirse = "PROXIMO".equals(estado) || "EN_CURSO".equals(estado);
        if (esAtleta && puedeInscribirse) {
            inscritoActual = c.isEstaInscrito();
            actualizarBotonInscripcion();
            btnInscripcion.setVisibility(View.VISIBLE);
        }

        // Lista inscritos (visible para todos)
        tvHeaderInscritos.setVisibility(View.VISIBLE);
        cargarInscritos();
    }

    private void actualizarBotonInscripcion() {
        if (inscritoActual) {
            btnInscripcion.setText(getString(R.string.btn_desinscribirse));
            btnInscripcion.setBackgroundTintList(
                    ColorStateList.valueOf(getColor(R.color.colorCancelled)));
        } else {
            btnInscripcion.setText(getString(R.string.btn_inscribirse));
            btnInscripcion.setBackgroundTintList(
                    ColorStateList.valueOf(getColor(R.color.colorPrimary)));
        }
        btnInscripcion.setOnClickListener(v -> toggleInscripcion());
    }

    private void toggleInscripcion() {
        btnInscripcion.setEnabled(false);

        if (inscritoActual) {
            ApiClient.getCompetenciasService().desinscribirse(competenciaId)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            btnInscripcion.setEnabled(true);
                            if (response.isSuccessful()) {
                                inscritoActual = false;
                                actualizarBotonInscripcion();
                                Toast.makeText(CompetenciaDetalleActivity.this,
                                        getString(R.string.msg_desinscripcion_ok),
                                        Toast.LENGTH_SHORT).show();
                                cargarInscritos();
                            } else {
                                Toast.makeText(CompetenciaDetalleActivity.this,
                                        getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            btnInscripcion.setEnabled(true);
                            Toast.makeText(CompetenciaDetalleActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            ApiClient.getCompetenciasService().inscribirse(competenciaId)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            btnInscripcion.setEnabled(true);
                            if (response.isSuccessful()) {
                                inscritoActual = true;
                                actualizarBotonInscripcion();
                                Toast.makeText(CompetenciaDetalleActivity.this,
                                        getString(R.string.msg_inscripcion_ok),
                                        Toast.LENGTH_SHORT).show();
                                cargarInscritos();
                            } else if (response.code() == 409) {
                                inscritoActual = true;
                                actualizarBotonInscripcion();
                            } else {
                                Toast.makeText(CompetenciaDetalleActivity.this,
                                        getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            btnInscripcion.setEnabled(true);
                            Toast.makeText(CompetenciaDetalleActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void cargarInscritos() {
        ApiClient.getCompetenciasService().getInscritos(competenciaId)
                .enqueue(new Callback<List<AtletaInfo>>() {
                    @Override
                    public void onResponse(Call<List<AtletaInfo>> call,
                                           Response<List<AtletaInfo>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<AtletaInfo> lista = response.body();
                            inscritosAdapter.setInscritos(lista);
                            String header = getString(R.string.lbl_atletas_inscritos)
                                    + " (" + lista.size() + ")";
                            tvHeaderInscritos.setText(header);
                            tvSinInscritos.setVisibility(
                                    lista.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                    }
                    @Override public void onFailure(Call<List<AtletaInfo>> call, Throwable t) {}
                });
    }

    private String estadoLabel(String e) {
        switch (e) {
            case "PROXIMO":    return "PRÓXIMO";
            case "EN_CURSO":   return "EN CURSO";
            case "FINALIZADO": return "FINALIZADO";
            case "CANCELADO":  return "CANCELADO";
            default: return e;
        }
    }

    private int estadoColor(String e) {
        switch (e) {
            case "PROXIMO":    return getColor(R.color.colorPrimary);
            case "EN_CURSO":   return getColor(R.color.colorActive);
            case "FINALIZADO": return getColor(R.color.colorTextSecondary);
            case "CANCELADO":  return getColor(R.color.colorCancelled);
            default:           return getColor(R.color.colorSurfaceVariant);
        }
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
