package com.example.tallerappmovil.atletas;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AtletasActivity extends AppCompatActivity {

    public static final String EXTRA_ATLETA_ID     = "atletaId";
    public static final String EXTRA_ATLETA_NOMBRE = "atletaNombre";

    private AtletasAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvVacio, tvContador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atletas);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBar);
        tvVacio     = findViewById(R.id.tvVacio);
        tvContador  = findViewById(R.id.tvContador);

        RecyclerView recycler = findViewById(R.id.recyclerAtletas);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AtletasAdapter();
        recycler.setAdapter(adapter);

        adapter.setOnItemClickListener(atleta -> {
            Intent intent = new Intent(this, AtletaPerfilActivity.class);
            intent.putExtra(EXTRA_ATLETA_ID, atleta.getId());
            intent.putExtra(EXTRA_ATLETA_NOMBRE, atleta.getNombreCompleto());
            startActivity(intent);
        });

        TextInputEditText etBusqueda = findViewById(R.id.etBusqueda);
        etBusqueda.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                adapter.filtrar(s.toString());
                actualizarContador();
            }
        });

        cargarAtletas();
    }

    private void cargarAtletas() {
        progressBar.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);

        ApiClient.getAtletasService().getAtletas().enqueue(new Callback<List<AtletaInfo>>() {
            @Override
            public void onResponse(Call<List<AtletaInfo>> call, Response<List<AtletaInfo>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<AtletaInfo> lista = response.body();
                    adapter.setAtletas(lista);
                    tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
                    actualizarContador();
                } else {
                    tvVacio.setVisibility(View.VISIBLE);
                    Toast.makeText(AtletasActivity.this,
                            getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<AtletaInfo>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                tvVacio.setVisibility(View.VISIBLE);
                Toast.makeText(AtletasActivity.this,
                        getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarContador() {
        int total = adapter.getTotalFiltrado();
        if (total > 0) {
            tvContador.setText(total + " " + getString(R.string.lbl_atletas_count));
            tvContador.setVisibility(View.VISIBLE);
        } else {
            tvContador.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
