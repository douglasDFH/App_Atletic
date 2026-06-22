package com.example.tallerappmovil.ranking;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.MarcaPersonal;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankingActivity extends AppCompatActivity {

    private RankingAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvVacio;
    private View layoutPodio;
    private TextView tvPodio1Nombre, tvPodio1Resultado;
    private TextView tvPodio2Nombre, tvPodio2Resultado;
    private TextView tvPodio3Nombre, tvPodio3Resultado;

    private String disciplinaSeleccionada;

    private static final String[] DISCIPLINAS = {
            "100m", "200m", "400m", "Salto Largo", "Lanzamiento de Bala", "Gimnasia"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar     = findViewById(R.id.progressBar);
        tvVacio         = findViewById(R.id.tvVacio);
        layoutPodio     = findViewById(R.id.layoutPodio);
        tvPodio1Nombre  = findViewById(R.id.tvPodio1Nombre);
        tvPodio1Resultado = findViewById(R.id.tvPodio1Resultado);
        tvPodio2Nombre  = findViewById(R.id.tvPodio2Nombre);
        tvPodio2Resultado = findViewById(R.id.tvPodio2Resultado);
        tvPodio3Nombre  = findViewById(R.id.tvPodio3Nombre);
        tvPodio3Resultado = findViewById(R.id.tvPodio3Resultado);

        RecyclerView recycler = findViewById(R.id.recyclerRanking);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RankingAdapter();
        recycler.setAdapter(adapter);

        // Pre-select disciplina si viene de MarcasActivity
        String disciplinaInicial = getIntent().getStringExtra("disciplina");
        int posInicial = 0;
        if (disciplinaInicial != null) {
            for (int i = 0; i < DISCIPLINAS.length; i++) {
                if (DISCIPLINAS[i].equals(disciplinaInicial)) { posInicial = i; break; }
            }
        }
        disciplinaSeleccionada = DISCIPLINAS[posInicial];

        setupSpinner(posInicial);
    }

    private void setupSpinner(int posInicial) {
        Spinner spinner = findViewById(R.id.spinnerDisciplina);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, DISCIPLINAS);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(posInicial, false);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                disciplinaSeleccionada = DISCIPLINAS[pos];
                cargarRanking();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        cargarRanking();
    }

    private void cargarRanking() {
        progressBar.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);
        layoutPodio.setVisibility(View.GONE);

        // Ranking: usa el endpoint dedicado que devuelve TODAS las marcas de la disciplina
        // (getMarcas limita al atleta a las suyas, no sirve para el leaderboard)
        ApiClient.getMarcasService().getRanking(disciplinaSeleccionada)
                .enqueue(new Callback<List<MarcaPersonal>>() {
                    @Override
                    public void onResponse(Call<List<MarcaPersonal>> call,
                                           Response<List<MarcaPersonal>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            List<MarcaPersonal> prs = filtrarPRs(response.body());
                            adapter.setEntradas(prs);
                            if (prs.isEmpty()) {
                                tvVacio.setVisibility(View.VISIBLE);
                            } else {
                                mostrarPodio(prs);
                            }
                        } else {
                            tvVacio.setVisibility(View.VISIBLE);
                            Toast.makeText(RankingActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<MarcaPersonal>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvVacio.setVisibility(View.VISIBLE);
                        Toast.makeText(RankingActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private List<MarcaPersonal> filtrarPRs(List<MarcaPersonal> todas) {
        List<MarcaPersonal> prs = new ArrayList<>();
        for (MarcaPersonal m : todas) {
            if (m.isEsMejorMarca()) prs.add(m);
        }
        // Si el backend no devuelve PRs marcados, muestra todas de todos modos
        return prs.isEmpty() ? todas : prs;
    }

    private void mostrarPodio(List<MarcaPersonal> lista) {
        if (lista.size() >= 1) {
            tvPodio1Nombre.setText(primerNombre(lista.get(0).getAtletaNombre()));
            tvPodio1Resultado.setText(formatResultado(lista.get(0)));
        }
        if (lista.size() >= 2) {
            tvPodio2Nombre.setText(primerNombre(lista.get(1).getAtletaNombre()));
            tvPodio2Resultado.setText(formatResultado(lista.get(1)));
        }
        if (lista.size() >= 3) {
            tvPodio3Nombre.setText(primerNombre(lista.get(2).getAtletaNombre()));
            tvPodio3Resultado.setText(formatResultado(lista.get(2)));
        }
        layoutPodio.setVisibility(View.VISIBLE);
    }

    private String primerNombre(String nombre) {
        if (nombre == null) return "—";
        String[] partes = nombre.trim().split(" ");
        return partes[0];
    }

    private String formatResultado(MarcaPersonal m) {
        String r = m.getResultado() != null ? m.getResultado() : "—";
        String u = m.getUnidad() != null ? " " + m.getUnidad() : "";
        return r + u;
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
