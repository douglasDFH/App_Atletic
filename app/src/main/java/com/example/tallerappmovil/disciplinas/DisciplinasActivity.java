package com.example.tallerappmovil.disciplinas;

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
import com.example.tallerappmovil.model.Disciplina;
import com.example.tallerappmovil.session.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisciplinasActivity extends AppCompatActivity {

    private DisciplinasAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvVacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disciplinas);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBar);
        tvVacio     = findViewById(R.id.tvVacio);

        RecyclerView recycler = findViewById(R.id.recyclerDisciplinas);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DisciplinasAdapter();
        recycler.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabNuevaDisciplina);
        SessionManager session = new SessionManager(this);
        String rol = session.getUserRole();

        if ("ENTRENADOR".equals(rol) || "ADMIN".equals(rol)) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(this, CrearEditarDisciplinaActivity.class);
                startActivity(intent);
            });
        }

        adapter.setOnItemClickListener(d -> {
            if ("ENTRENADOR".equals(rol) || "ADMIN".equals(rol)) {
                Intent intent = new Intent(this, CrearEditarDisciplinaActivity.class);
                intent.putExtra("disciplinaId", d.getId());
                startActivity(intent);
            }
        });

        cargarDisciplinas(rol);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String rol = new SessionManager(this).getUserRole();
        cargarDisciplinas(rol);
    }

    private void cargarDisciplinas(String rol) {
        progressBar.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);

        Call<List<Disciplina>> call;
        if ("ENTRENADOR".equals(rol) || "ADMIN".equals(rol)) {
            call = ApiClient.getDisciplinasService().listarTodas();
        } else {
            call = ApiClient.getDisciplinasService().listar();
        }

        call.enqueue(new Callback<List<Disciplina>>() {
            @Override
            public void onResponse(Call<List<Disciplina>> c, Response<List<Disciplina>> r) {
                progressBar.setVisibility(View.GONE);
                if (r.isSuccessful() && r.body() != null) {
                    List<Disciplina> lista = r.body();
                    adapter.setDisciplinas(lista);
                    tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    tvVacio.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Disciplina>> c, Throwable t) {
                progressBar.setVisibility(View.GONE);
                tvVacio.setVisibility(View.VISIBLE);
                Toast.makeText(DisciplinasActivity.this,
                        "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
