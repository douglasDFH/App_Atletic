package com.example.tallerappmovil.grupos;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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
import com.example.tallerappmovil.asistencia.ReporteAsistenciaActivity;
import com.example.tallerappmovil.atletas.AtletaPerfilActivity;
import com.example.tallerappmovil.atletas.AtletasActivity;
import com.example.tallerappmovil.atletas.AtletasAdapter;
import com.example.tallerappmovil.model.AtletaInfo;
import com.example.tallerappmovil.model.GrupoDetalle;
import com.example.tallerappmovil.session.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GrupoDetalleActivity extends AppCompatActivity {

    private static final int REQ_GESTIONAR_ATLETAS = 200;

    private TextView tvInicialHeader, tvNombreHeader, tvDisciplinaHeader;
    private TextView tvDescripcionHeader, tvTotalAtletas, tvSinAtletas;
    private ProgressBar progressBar;
    private AtletasAdapter atletasAdapter;
    private List<AtletaInfo> atletasActuales = new java.util.ArrayList<>();
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
        if (requestCode == REQ_GESTIONAR_ATLETAS && grupoId != null) {
            cargarDetalle(grupoId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_grupo_detalle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_reporte) {
            Intent intent = new Intent(this, ReporteAsistenciaActivity.class);
            if (grupoId != null) {
                intent.putExtra(ReporteAsistenciaActivity.EXTRA_GRUPO_ID, grupoId);
                intent.putExtra(ReporteAsistenciaActivity.EXTRA_GRUPO_NOMBRE,
                        getIntent().getStringExtra(GruposActivity.EXTRA_GRUPO_NOMBRE));
            }
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
