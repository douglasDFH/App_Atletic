package com.example.tallerappmovil.eventos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.agenda.AgendaActivity;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.dashboard.DashboardActivity;
import com.example.tallerappmovil.marcas.MarcasActivity;
import com.example.tallerappmovil.model.Competencia;
import com.example.tallerappmovil.perfil.PerfilActivity;
import com.example.tallerappmovil.session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventosActivity extends AppCompatActivity {

    private EventosAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvVacio;

    private String estadoFiltro = null;

    private static final String[] ESTADOS_LABELS = {
            "Todos", "Próximos", "En curso", "Finalizados", "Cancelados"
    };
    private static final String[] ESTADOS_API = {
            null, "PROXIMO", "EN_CURSO", "FINALIZADO", "CANCELADO"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        SessionManager session = new SessionManager(this);
        String rol = session.getUserRole();
        boolean esEntrenador = "ENTRENADOR".equals(rol) || "ADMIN".equals(rol);

        progressBar = findViewById(R.id.progressBar);
        tvVacio     = findViewById(R.id.tvVacio);

        RecyclerView recycler = findViewById(R.id.recyclerEventos);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventosAdapter();
        recycler.setAdapter(adapter);

        adapter.setOnItemClickListener(c -> {
            Intent intent = new Intent(this, CompetenciaDetalleActivity.class);
            intent.putExtra(CompetenciaDetalleActivity.EXTRA_ID, c.getId());
            intent.putExtra(CompetenciaDetalleActivity.EXTRA_NOMBRE, c.getNombre());
            startActivity(intent);
        });

        setupSpinnerEstado();

        FloatingActionButton fab = findViewById(R.id.fabNuevoEvento);
        if (esEntrenador) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v ->
                    startActivity(new Intent(this, CrearCompetenciaActivity.class)));
        }

        setupBottomNav();
        cargarEventos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_eventos);
        cargarEventos();
    }

    private void setupSpinnerEstado() {
        Spinner spinner = findViewById(R.id.spinnerEstado);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, ESTADOS_LABELS);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                estadoFiltro = ESTADOS_API[pos];
                cargarEventos();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void cargarEventos() {
        progressBar.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);

        ApiClient.getCompetenciasService().getCompetencias(estadoFiltro)
                .enqueue(new Callback<List<Competencia>>() {
                    @Override
                    public void onResponse(Call<List<Competencia>> call,
                                           Response<List<Competencia>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            List<Competencia> lista = response.body();
                            adapter.setCompetencias(lista);
                            tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
                        } else {
                            tvVacio.setVisibility(View.VISIBLE);
                            Toast.makeText(EventosActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Competencia>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvVacio.setVisibility(View.VISIBLE);
                        Toast.makeText(EventosActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_eventos) return true;
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, DashboardActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            } else if (id == R.id.nav_agenda) {
                startActivity(new Intent(this, AgendaActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            } else if (id == R.id.nav_marcas) {
                startActivity(new Intent(this, MarcasActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            }
            overridePendingTransition(0, 0);
            return false;
        });
        nav.setSelectedItemId(R.id.nav_eventos);
    }
}
