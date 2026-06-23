package com.example.tallerappmovil.eventos;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

    private static final int REQ_EDITAR = 201;

    private static final SimpleDateFormat ISO  =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat FULL =
            new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es"));

    private Long competenciaId;
    private boolean esAtleta;
    private boolean esEntrenador;
    private boolean inscritoActual = false;
    private Competencia competenciaActual;

    private TextView tvNombre, tvEstado, tvDisciplina, tvFecha, tvCategoria, tvLugar;
    private TextView tvDescripcion, tvHeaderInscritos, tvSinInscritos;
    private View layoutDescripcion;
    private MaterialButton btnInscripcion;
    private ProgressBar progressBar;
    private View scrollContent;
    private View layoutBotonesEntrenador;
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
        esAtleta     = "ATLETA".equals(rol);
        esEntrenador = "ENTRENADOR".equals(rol) || "ADMIN".equals(rol);

        tvNombre              = findViewById(R.id.tvNombreDetalle);
        tvEstado              = findViewById(R.id.tvEstadoDetalle);
        tvDisciplina          = findViewById(R.id.tvDisciplinaDetalle);
        tvFecha               = findViewById(R.id.tvFechaDetalle);
        tvCategoria           = findViewById(R.id.tvCategoriaDetalle);
        tvLugar               = findViewById(R.id.tvLugarDetalle);
        tvDescripcion         = findViewById(R.id.tvDescripcionDetalle);
        layoutDescripcion     = findViewById(R.id.layoutDescripcion);
        btnInscripcion        = findViewById(R.id.btnInscripcion);
        tvHeaderInscritos     = findViewById(R.id.tvHeaderInscritos);
        tvSinInscritos        = findViewById(R.id.tvSinInscritos);
        progressBar           = findViewById(R.id.progressBar);
        scrollContent         = findViewById(R.id.scrollContent);
        layoutBotonesEntrenador = findViewById(R.id.layoutBotonesEntrenador);

        RecyclerView recycler = findViewById(R.id.recyclerInscritos);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        inscritosAdapter = new InscritosAdapter();
        recycler.setAdapter(inscritosAdapter);

        if (competenciaId != -1L) cargarDetalle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_EDITAR && resultCode == RESULT_OK) {
            cargarDetalle();
        }
    }

    private void cargarDetalle() {
        progressBar.setVisibility(View.VISIBLE);
        scrollContent.setVisibility(View.GONE);

        ApiClient.getCompetenciasService().getCompetencia(competenciaId)
                .enqueue(new Callback<Competencia>() {
                    @Override
                    public void onResponse(Call<Competencia> call, Response<Competencia> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            competenciaActual = response.body();
                            mostrarDetalle(competenciaActual);
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

        try {
            Date d = ISO.parse(c.getFechaEvento());
            tvFecha.setText(d != null ? FULL.format(d) : c.getFechaEvento());
        } catch (Exception e) {
            tvFecha.setText(c.getFechaEvento() != null ? c.getFechaEvento() : "--");
        }

        String estado = c.getEstado() != null ? c.getEstado() : "";
        tvEstado.setText(estadoLabel(estado));
        tvEstado.setBackgroundTintList(ColorStateList.valueOf(estadoColor(estado)));
        tvEstado.setVisibility(View.VISIBLE);

        if (c.getDescripcion() != null && !c.getDescripcion().isEmpty()) {
            tvDescripcion.setText(c.getDescripcion());
            layoutDescripcion.setVisibility(View.VISIBLE);
        } else {
            layoutDescripcion.setVisibility(View.GONE);
        }

        // Botón inscripción (solo atleta, eventos próximos o en curso)
        boolean puedeInscribirse = "PROXIMO".equals(estado) || "EN_CURSO".equals(estado);
        if (esAtleta && puedeInscribirse) {
            inscritoActual = c.isEstaInscrito();
            actualizarBotonInscripcion();
            btnInscripcion.setVisibility(View.VISIBLE);
        } else {
            btnInscripcion.setVisibility(View.GONE);
        }

        // Ver / registrar resultados (todos; el entrenador puede registrar)
        findViewById(R.id.btnResultados).setOnClickListener(v -> {
            Intent i = new Intent(this, ResultadosCompetenciaActivity.class);
            i.putExtra(ResultadosCompetenciaActivity.EXTRA_COMP_ID, competenciaId);
            i.putExtra(ResultadosCompetenciaActivity.EXTRA_COMP_NOMBRE, c.getNombre());
            i.putExtra(ResultadosCompetenciaActivity.EXTRA_PUEDE_EDITAR, esEntrenador);
            startActivity(i);
        });

        // Botones editar/eliminar (solo entrenador)
        if (esEntrenador) {
            layoutBotonesEntrenador.setVisibility(View.VISIBLE);
            findViewById(R.id.btnEditarCompetencia).setOnClickListener(v -> abrirEditar(c));
            findViewById(R.id.btnEliminarCompetencia).setOnClickListener(v -> confirmarEliminar());
        }

        tvHeaderInscritos.setVisibility(View.VISIBLE);
        cargarInscritos();
    }

    private void abrirEditar(Competencia c) {
        Intent intent = new Intent(this, CrearCompetenciaActivity.class);
        intent.putExtra(CrearCompetenciaActivity.EXTRA_COMPETENCIA_ID, c.getId());
        intent.putExtra(CrearCompetenciaActivity.EXTRA_COMPETENCIA_NOMBRE, c.getNombre());
        intent.putExtra(CrearCompetenciaActivity.EXTRA_DISCIPLINA, c.getDisciplina());
        intent.putExtra(CrearCompetenciaActivity.EXTRA_FECHA, c.getFechaEvento());
        intent.putExtra(CrearCompetenciaActivity.EXTRA_LUGAR, c.getLugar());
        intent.putExtra(CrearCompetenciaActivity.EXTRA_CATEGORIA, c.getCategoria());
        intent.putExtra(CrearCompetenciaActivity.EXTRA_DESCRIPCION, c.getDescripcion());
        startActivityForResult(intent, REQ_EDITAR);
    }

    private void confirmarEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar evento")
                .setMessage(getString(R.string.confirm_eliminar_competencia))
                .setPositiveButton("Eliminar", (d, w) -> eliminarCompetencia())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarCompetencia() {
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getCompetenciasService().eliminarCompetencia(competenciaId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            Toast.makeText(CompetenciaDetalleActivity.this,
                                    getString(R.string.msg_competencia_eliminada),
                                    Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(CompetenciaDetalleActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CompetenciaDetalleActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
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
