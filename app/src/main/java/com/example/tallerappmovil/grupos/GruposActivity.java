package com.example.tallerappmovil.grupos;

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
import com.example.tallerappmovil.model.GrupoEntrenamiento;
import com.example.tallerappmovil.session.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GruposActivity extends AppCompatActivity {

    public static final String EXTRA_GRUPO_ID     = "grupoId";
    public static final String EXTRA_GRUPO_NOMBRE = "grupoNombre";

    private GruposAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvVacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupos);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SessionManager session = new SessionManager(this);
        String rol = session.getUserRole();
        boolean esEntrenador = "ENTRENADOR".equals(rol) || "ADMIN".equals(rol);

        progressBar = findViewById(R.id.progressBar);
        tvVacio     = findViewById(R.id.tvVacio);

        RecyclerView recycler = findViewById(R.id.recyclerGrupos);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GruposAdapter();
        recycler.setAdapter(adapter);

        adapter.setOnItemClickListener(grupo -> {
            Intent intent = new Intent(this, GrupoDetalleActivity.class);
            intent.putExtra(EXTRA_GRUPO_ID, grupo.getId());
            intent.putExtra(EXTRA_GRUPO_NOMBRE, grupo.getNombre());
            startActivity(intent);
        });

        FloatingActionButton fab = findViewById(R.id.fabNuevoGrupo);
        if (esEntrenador) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v ->
                    startActivityForResult(new Intent(this, CrearGrupoActivity.class), 1));
        }

        cargarGrupos();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) cargarGrupos();
    }

    private void cargarGrupos() {
        progressBar.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);

        ApiClient.getGruposService().listarGrupos()
                .enqueue(new Callback<List<GrupoEntrenamiento>>() {
                    @Override
                    public void onResponse(Call<List<GrupoEntrenamiento>> call,
                                           Response<List<GrupoEntrenamiento>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            List<GrupoEntrenamiento> lista = response.body();
                            adapter.setGrupos(lista);
                            tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
                        } else {
                            tvVacio.setVisibility(View.VISIBLE);
                            Toast.makeText(GruposActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<GrupoEntrenamiento>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvVacio.setVisibility(View.VISIBLE);
                        Toast.makeText(GruposActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
