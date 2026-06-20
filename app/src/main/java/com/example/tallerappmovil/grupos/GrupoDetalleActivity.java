package com.example.tallerappmovil.grupos;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.tallerappmovil.asistencia.ReporteAsistenciaActivity;
import com.example.tallerappmovil.atletas.AtletaPerfilActivity;
import com.example.tallerappmovil.atletas.AtletasActivity;
import com.example.tallerappmovil.atletas.AtletasAdapter;
import com.example.tallerappmovil.model.AtletaInfo;
import com.example.tallerappmovil.model.GrupoDetalle;
import com.example.tallerappmovil.session.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GrupoDetalleActivity extends AppCompatActivity {

    private static final int REQ_GESTIONAR_ATLETAS = 200;
    private static final int REQ_EDITAR_GRUPO      = 201;

    private String grupoNombreActual;
    private String grupoDisciplinaActual;
    private String grupoDescripcionActual;

    private TextView tvInicialHeader, tvNombreHeader, tvDisciplinaHeader;
    private TextView tvDescripcionHeader, tvTotalAtletas, tvSinAtletas;
    private ProgressBar progressBar;
    private AtletasAdapter atletasAdapter;
    private List<AtletaInfo> atletasActuales = new ArrayList<>();
    private Long grupoId;
    private boolean esEntrenador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo_detalle);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String rol = new SessionManager(this).getUserRole();
        esEntrenador = "ENTRENADOR".equals(rol) || "ADMIN".equals(rol);

        grupoId = getIntent().getLongExtra(GruposActivity.EXTRA_GRUPO_ID, -1L);
        if (grupoId == -1L) grupoId = null;
        String grupoNombre = getIntent().getStringExtra(GruposActivity.EXTRA_GRUPO_NOMBRE);

        tvInicialHeader    = findViewById(R.id.tvInicialHeader);
        tvNombreHeader     = findViewById(R.id.tvNombreHeader);
        tvDisciplinaHeader = findViewById(R.id.tvDisciplinaHeader);
        tvDescripcionHeader = findViewById(R.id.tvDescripcionHeader);
        tvTotalAtletas     = findViewById(R.id.tvTotalAtletas);
        tvSinAtletas       = findViewById(R.id.tvSinAtletas);
        progressBar        = findViewById(R.id.progressBar);

        if (grupoNombre != null) {
            tvNombreHeader.setText(grupoNombre);
            tvInicialHeader.setText(String.valueOf(grupoNombre.charAt(0)).toUpperCase(Locale.getDefault()));
            if (getSupportActionBar() != null) getSupportActionBar().setTitle(grupoNombre);
        }

        RecyclerView recycler = findViewById(R.id.recyclerAtletas);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        atletasAdapter = new AtletasAdapter();
        recycler.setAdapter(atletasAdapter);

        atletasAdapter.setOnItemClickListener(atleta -> {
            Intent intent = new Intent(this, AtletaPerfilActivity.class);
            intent.putExtra(AtletasActivity.EXTRA_ATLETA_ID, atleta.getId());
            intent.putExtra(AtletasActivity.EXTRA_ATLETA_NOMBRE, atleta.getNombreCompleto());
            startActivity(intent);
        });

        // FAB gestionar atletas (solo entrenador)
        FloatingActionButton fab = findViewById(R.id.fabGestionarAtletas);
        if (esEntrenador) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v -> abrirSeleccionAtletas());
        }

        if (grupoId != null) cargarDetalle(grupoId);
    }

    private void cargarDetalle(Long grupoId) {
        progressBar.setVisibility(View.VISIBLE);

        ApiClient.getGruposService().getGrupo(grupoId)
                .enqueue(new Callback<GrupoDetalle>() {
                    @Override
                    public void onResponse(Call<GrupoDetalle> call, Response<GrupoDetalle> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            GrupoDetalle d = response.body();

                            String nombre = d.getNombre() != null ? d.getNombre() : "";
                            grupoNombreActual      = nombre;
                            grupoDisciplinaActual  = d.getDisciplina();
                            grupoDescripcionActual = d.getDescripcion();

                            tvNombreHeader.setText(nombre);
                            if (!nombre.isEmpty()) {
                                tvInicialHeader.setText(
                                        String.valueOf(nombre.charAt(0)).toUpperCase(Locale.getDefault()));
                                if (getSupportActionBar() != null)
                                    getSupportActionBar().setTitle(nombre);
                            }

                            if (d.getDisciplina() != null && !d.getDisciplina().isEmpty()) {
                                tvDisciplinaHeader.setText(d.getDisciplina());
                            }

                            if (d.getDescripcion() != null && !d.getDescripcion().isEmpty()) {
                                tvDescripcionHeader.setText(d.getDescripcion());
                                tvDescripcionHeader.setVisibility(View.VISIBLE);
                            }

                            if (d.getAtletas() != null) {
                                atletasActuales = d.getAtletas();
                                int total = atletasActuales.size();
                                tvTotalAtletas.setText(String.valueOf(total));
                                atletasAdapter.setAtletas(atletasActuales);
                                tvSinAtletas.setVisibility(total == 0 ? View.VISIBLE : View.GONE);
                            } else {
                                atletasActuales = new java.util.ArrayList<>();
                                tvTotalAtletas.setText("0");
                                tvSinAtletas.setVisibility(View.VISIBLE);
                            }
                        } else {
                            tvSinAtletas.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(Call<GrupoDetalle> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvSinAtletas.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void abrirSeleccionAtletas() {
        long[] ids = new long[atletasActuales.size()];
        for (int i = 0; i < atletasActuales.size(); i++) {
            ids[i] = atletasActuales.get(i).getId();
        }
        Intent intent = new Intent(this, SeleccionarAtletasActivity.class);
        intent.putExtra(SeleccionarAtletasActivity.EXTRA_GRUPO_ID, grupoId != null ? grupoId : -1L);
        intent.putExtra(SeleccionarAtletasActivity.EXTRA_ATLETAS_IDS, ids);
        startActivityForResult(intent, REQ_GESTIONAR_ATLETAS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && grupoId != null) {
            cargarDetalle(grupoId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_grupo_detalle, menu);
        menu.findItem(R.id.action_editar_grupo).setVisible(esEntrenador);
        menu.findItem(R.id.action_eliminar_grupo).setVisible(esEntrenador);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_editar_grupo) {
            Intent intent = new Intent(this, CrearGrupoActivity.class);
            if (grupoId != null) {
                intent.putExtra(CrearGrupoActivity.EXTRA_GRUPO_ID, (long) grupoId);
                intent.putExtra(CrearGrupoActivity.EXTRA_NOMBRE, grupoNombreActual);
                intent.putExtra(CrearGrupoActivity.EXTRA_DISCIPLINA, grupoDisciplinaActual);
                intent.putExtra(CrearGrupoActivity.EXTRA_DESCRIPCION, grupoDescripcionActual);
            }
            startActivityForResult(intent, REQ_EDITAR_GRUPO);
            return true;
        }
        if (id == R.id.action_reporte) {
            Intent intent = new Intent(this, ReporteAsistenciaActivity.class);
            if (grupoId != null) {
                intent.putExtra(ReporteAsistenciaActivity.EXTRA_GRUPO_ID, grupoId);
                intent.putExtra(ReporteAsistenciaActivity.EXTRA_GRUPO_NOMBRE, grupoNombreActual);
            }
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_eliminar_grupo) {
            confirmarEliminarGrupo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmarEliminarGrupo() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar grupo")
                .setMessage("¿Eliminar el grupo \"" + grupoNombreActual + "\"? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (d, w) -> eliminarGrupo())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarGrupo() {
        if (grupoId == null) return;
        ApiClient.getGruposService().eliminarGrupo(grupoId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(GrupoDetalleActivity.this,
                                    "Grupo eliminado", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(GrupoDetalleActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                        Toast.makeText(GrupoDetalleActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
