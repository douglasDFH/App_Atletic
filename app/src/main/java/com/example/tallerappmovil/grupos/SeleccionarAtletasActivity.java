package com.example.tallerappmovil.grupos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.AtletaInfo;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeleccionarAtletasActivity extends AppCompatActivity {

    public static final String EXTRA_GRUPO_ID    = "grupo_id";
    public static final String EXTRA_ATLETAS_IDS = "atletas_ids";

    private Long grupoId;
    private SeleccionAtletaAdapter adapter;
    private Toolbar toolbar;
    private View progressBar;
    private android.widget.TextView tvVacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_atletas);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        grupoId     = getIntent().getLongExtra(EXTRA_GRUPO_ID, -1L);
        long[] ids  = getIntent().getLongArrayExtra(EXTRA_ATLETAS_IDS);
        Set<Long> idsEnGrupo = new HashSet<>();
        if (ids != null) for (long id : ids) idsEnGrupo.add(id);

        progressBar = findViewById(R.id.progressBar);
        tvVacio     = findViewById(R.id.tvVacio);

        RecyclerView recycler = findViewById(R.id.recyclerAtletas);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SeleccionAtletaAdapter();
        recycler.setAdapter(adapter);

        adapter.setOnToggleListener((atleta, estaEnGrupo) -> toggleAtleta(atleta, estaEnGrupo));

        TextInputEditText etBuscar = findViewById(R.id.etBuscar);
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                adapter.filtrar(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        cargarAtletas(idsEnGrupo);
    }

    private void cargarAtletas(Set<Long> idsEnGrupo) {
        progressBar.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);

        ApiClient.getAtletasService().getAtletas()
                .enqueue(new Callback<List<AtletaInfo>>() {
                    @Override
                    public void onResponse(Call<List<AtletaInfo>> call,
                                           Response<List<AtletaInfo>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null
                                && !response.body().isEmpty()) {
                            adapter.setAtletas(response.body(), idsEnGrupo);
                            actualizarSubtitulo();
                        } else {
                            tvVacio.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AtletaInfo>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvVacio.setVisibility(View.VISIBLE);
                        Toast.makeText(SeleccionarAtletasActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void toggleAtleta(AtletaInfo atleta, boolean estaEnGrupo) {
        adapter.setCargando(atleta.getId(), true);

        Callback<Void> callback = new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    adapter.setEnGrupo(atleta.getId(), !estaEnGrupo);
                    actualizarSubtitulo();
                    setResult(RESULT_OK);
                } else {
                    adapter.setCargando(atleta.getId(), false);
                    Toast.makeText(SeleccionarAtletasActivity.this,
                            getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                adapter.setCargando(atleta.getId(), false);
                Toast.makeText(SeleccionarAtletasActivity.this,
                        getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
            }
        };

        if (estaEnGrupo) {
            ApiClient.getGruposService()
                    .quitarAtleta(grupoId, atleta.getId())
                    .enqueue(callback);
        } else {
            ApiClient.getGruposService()
                    .agregarAtleta(grupoId, atleta.getId())
                    .enqueue(callback);
        }
    }

    private void actualizarSubtitulo() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(adapter.countEnGrupo() + " en el grupo");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
