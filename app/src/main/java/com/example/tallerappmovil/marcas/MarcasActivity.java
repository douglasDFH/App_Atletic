package com.example.tallerappmovil.marcas;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.tallerappmovil.utils.PdfReportGenerator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.agenda.AgendaActivity;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.ranking.RankingActivity;
import com.example.tallerappmovil.dashboard.DashboardActivity;
import com.example.tallerappmovil.eventos.EventosActivity;
import com.example.tallerappmovil.model.AtletaInfo;
import com.example.tallerappmovil.model.MarcaPersonal;
import com.example.tallerappmovil.perfil.PerfilActivity;
import com.example.tallerappmovil.session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarcasActivity extends AppCompatActivity {

    private MarcasAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvVacio;
    private View cardMejorMarca;
    private TextView tvMejorMarcaTexto;

    private boolean esEntrenador;
    private String disciplinaSeleccionada = null;
    private Long atletaSeleccionadoId = null;
    private List<AtletaInfo> atletasList = new ArrayList<>();

    private static final String[] DISCIPLINAS = {
            "100m", "200m", "400m", "Salto Largo", "Lanzamiento de Bala", "Gimnasia"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marcas);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        SessionManager session = new SessionManager(this);
        String rol = session.getUserRole();
        esEntrenador = "ENTRENADOR".equals(rol) || "ADMIN".equals(rol);

        progressBar      = findViewById(R.id.progressBar);
        tvVacio          = findViewById(R.id.tvVacio);
        cardMejorMarca   = findViewById(R.id.cardMejorMarca);
        tvMejorMarcaTexto = findViewById(R.id.tvMejorMarcaTexto);

        RecyclerView recycler = findViewById(R.id.recyclerMarcas);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MarcasAdapter();
        adapter.setMostrarAtleta(esEntrenador);
        if (esEntrenador) {
            adapter.setMostrarEliminar(true, marca -> confirmarEliminarMarca(marca));
        }
        recycler.setAdapter(adapter);

        setupSpinnerDisciplina();

        FloatingActionButton fab = findViewById(R.id.fabNuevaMarca);
        if (esEntrenador) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v ->
                    startActivity(new Intent(this, RegistrarMarcaActivity.class)));
            setupSpinnerAtleta();
        }

        setupBottomNav();
        cargarMarcas();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_marcas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_ranking) {
            Intent intent = new Intent(this, RankingActivity.class);
            if (disciplinaSeleccionada != null) intent.putExtra("disciplina", disciplinaSeleccionada);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.action_evolucion) {
            Intent intent = new Intent(this, EvolucionMarcasActivity.class);
            if (disciplinaSeleccionada != null)
                intent.putExtra(EvolucionMarcasActivity.EXTRA_DISCIPLINA, disciplinaSeleccionada);
            if (atletaSeleccionadoId != null)
                intent.putExtra(EvolucionMarcasActivity.EXTRA_ATLETA_ID, atletaSeleccionadoId);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.action_export_pdf) {
            List<MarcaPersonal> lista = adapter.getMarcas();
            if (lista == null || lista.isEmpty()) {
                Toast.makeText(this, "No hay marcas para exportar", Toast.LENGTH_SHORT).show();
            } else {
                String titulo = disciplinaSeleccionada != null ? disciplinaSeleccionada : "Todas las disciplinas";
                PdfReportGenerator.exportarMarcas(this, lista, titulo);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_marcas);
        cargarMarcas();
    }

    private void setupSpinnerDisciplina() {
        Spinner spinner = findViewById(R.id.spinnerDisciplina);
        List<String> opciones = new ArrayList<>();
        opciones.add("Todas");
        for (String d : DISCIPLINAS) opciones.add(d);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, opciones);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                disciplinaSeleccionada = pos == 0 ? null : opciones.get(pos);
                cargarMarcas();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSpinnerAtleta() {
        TextInputLayout tilAtleta = findViewById(R.id.tilAtleta);
        tilAtleta.setVisibility(View.VISIBLE);
        AutoCompleteTextView spinnerAtleta = findViewById(R.id.spinnerAtleta);

        ApiClient.getMarcasService().getAtletas().enqueue(new Callback<List<AtletaInfo>>() {
            @Override
            public void onResponse(Call<List<AtletaInfo>> call, Response<List<AtletaInfo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    atletasList = response.body();
                    List<String> nombres = new ArrayList<>();
                    nombres.add("Todos los atletas");
                    for (AtletaInfo a : atletasList) nombres.add(a.getNombreCompleto());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            MarcasActivity.this,
                            android.R.layout.simple_dropdown_item_1line, nombres);
                    spinnerAtleta.setAdapter(adapter);

                    spinnerAtleta.setOnItemClickListener((parent, view, position, id) -> {
                        atletaSeleccionadoId = position == 0 ? null : atletasList.get(position - 1).getId();
                        cargarMarcas();
                    });
                }
            }
            @Override
            public void onFailure(Call<List<AtletaInfo>> call, Throwable t) {}
        });
    }

    private void cargarMarcas() {
        progressBar.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);
        cardMejorMarca.setVisibility(View.GONE);

        Callback<List<MarcaPersonal>> callback = new Callback<List<MarcaPersonal>>() {
            @Override
            public void onResponse(Call<List<MarcaPersonal>> call, Response<List<MarcaPersonal>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<MarcaPersonal> lista = response.body();
                    adapter.setMarcas(lista);
                    tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
                    mostrarMejorMarca(lista);
                } else {
                    tvVacio.setVisibility(View.VISIBLE);
                    Toast.makeText(MarcasActivity.this,
                            getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<MarcaPersonal>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                tvVacio.setVisibility(View.VISIBLE);
                Toast.makeText(MarcasActivity.this,
                        getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
            }
        };

        if (esEntrenador && atletaSeleccionadoId != null) {
            ApiClient.getMarcasService()
                    .getMarcasPorAtleta(atletaSeleccionadoId, disciplinaSeleccionada)
                    .enqueue(callback);
        } else {
            ApiClient.getMarcasService()
                    .getMarcas(disciplinaSeleccionada)
                    .enqueue(callback);
        }
    }

    private void mostrarMejorMarca(List<MarcaPersonal> lista) {
        for (MarcaPersonal m : lista) {
            if (m.isEsMejorMarca()) {
                String unidad = m.getUnidad() != null ? " " + m.getUnidad() : "";
                tvMejorMarcaTexto.setText(m.getDisciplina() + ": " +
                        m.getResultado() + unidad);
                cardMejorMarca.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    private void confirmarEliminarMarca(com.example.tallerappmovil.model.MarcaPersonal marca) {
        String disc = marca.getDisciplina() != null ? marca.getDisciplina() : "marca";
        String res  = marca.getResultado() != null ? " (" + marca.getResultado() + ")" : "";
        new AlertDialog.Builder(this)
                .setTitle("Eliminar marca")
                .setMessage("¿Eliminar la marca de " + disc + res + "? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (d, w) -> eliminarMarca(marca.getId()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarMarca(Long id) {
        ApiClient.getMarcasService().eliminarMarca(id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            adapter.eliminarItem(id);
                            Toast.makeText(MarcasActivity.this,
                                    "Marca eliminada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MarcasActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(MarcasActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_marcas) return true;
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, DashboardActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            } else if (id == R.id.nav_agenda) {
                startActivity(new Intent(this, AgendaActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            } else if (id == R.id.nav_eventos) {
                startActivity(new Intent(this, EventosActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            }
            overridePendingTransition(0, 0);
            return false;
        });
        nav.setSelectedItemId(R.id.nav_marcas);
    }
}
