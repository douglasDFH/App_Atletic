package com.example.tallerappmovil.agenda;

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
import com.example.tallerappmovil.asistencia.AsistenciaActivity;
import com.example.tallerappmovil.dashboard.DashboardActivity;
import com.example.tallerappmovil.eventos.EventosActivity;
import com.example.tallerappmovil.marcas.MarcasActivity;
import com.example.tallerappmovil.model.SesionEntrenamiento;
import com.example.tallerappmovil.perfil.PerfilActivity;
import com.example.tallerappmovil.session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgendaActivity extends AppCompatActivity {

    private RecyclerView recyclerSesiones;
    private SesionAdapter adapter;
    private TextView tvSemana, tvVacio;
    private ProgressBar progressBar;

    private final Calendar semanaActual = Calendar.getInstance();
    private String userRol;

    private static final SimpleDateFormat API_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat DISPLAY_FORMAT =
            new SimpleDateFormat("dd MMM", new Locale("es"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userRol = new SessionManager(this).getUserRole();
        boolean puedeEditar = "ENTRENADOR".equals(userRol) || "ADMIN".equals(userRol);

        recyclerSesiones = findViewById(R.id.recyclerSesiones);
        tvSemana         = findViewById(R.id.tvSemana);
        tvVacio          = findViewById(R.id.tvVacio);
        progressBar      = findViewById(R.id.progressBar);
        FloatingActionButton fab = findViewById(R.id.fabCrear);

        fab.setVisibility(puedeEditar ? View.VISIBLE : View.GONE);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, CrearSesionActivity.class)));

        recyclerSesiones.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SesionAdapter(puedeEditar, sesion -> {
            if (!puedeEditar) return;
            String titulo = sesion.getGrupoNombre() != null ? sesion.getGrupoNombre() : "Sesión";
            String[] opciones = {
                    getString(R.string.opc_editar_sesion),
                    getString(R.string.opc_tomar_asistencia)
            };
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle(titulo)
                    .setItems(opciones, (d, which) -> {
                        if (which == 0) {
                            Intent intent = new Intent(this, CrearSesionActivity.class);
                            intent.putExtra(CrearSesionActivity.EXTRA_SESION_ID, sesion.getId());
                            intent.putExtra(CrearSesionActivity.EXTRA_HORA_INICIO, sesion.getHoraInicio());
                            intent.putExtra(CrearSesionActivity.EXTRA_HORA_FIN, sesion.getHoraFin());
                            intent.putExtra(CrearSesionActivity.EXTRA_LUGAR, sesion.getLugar());
                            intent.putExtra(CrearSesionActivity.EXTRA_GRUPO_ID, sesion.getGrupoId());
                            intent.putExtra(CrearSesionActivity.EXTRA_DESCRIPCION, sesion.getDescripcion());
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(this, AsistenciaActivity.class);
                            intent.putExtra(AsistenciaActivity.EXTRA_SESION_ID, sesion.getId());
                            intent.putExtra(AsistenciaActivity.EXTRA_GRUPO, sesion.getGrupoNombre());
                            intent.putExtra(AsistenciaActivity.EXTRA_HORA, sesion.getHoraInicio());
                            intent.putExtra(AsistenciaActivity.EXTRA_LUGAR, sesion.getLugar());
                            startActivity(intent);
                        }
                    })
                    .show();
        });
        recyclerSesiones.setAdapter(adapter);

        moverALunes(semanaActual);

        findViewById(R.id.btnPrevSemana).setOnClickListener(v -> {
            semanaActual.add(Calendar.WEEK_OF_YEAR, -1);
            cargarSesiones();
        });
        findViewById(R.id.btnNextSemana).setOnClickListener(v -> {
            semanaActual.add(Calendar.WEEK_OF_YEAR, 1);
            cargarSesiones();
        });

        setupBottomNav();
        cargarSesiones();
    }

    private void moverALunes(Calendar cal) {
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DATE, -1);
        }
    }

    private void cargarSesiones() {
        String fechaApi = API_FORMAT.format(semanaActual.getTime());

        Calendar fin = (Calendar) semanaActual.clone();
        fin.add(Calendar.DATE, 6);
        String texto = DISPLAY_FORMAT.format(semanaActual.getTime()).toUpperCase(Locale.getDefault())
                + " — "
                + DISPLAY_FORMAT.format(fin.getTime()).toUpperCase(Locale.getDefault())
                + " " + semanaActual.get(Calendar.YEAR);
        tvSemana.setText(texto);

        progressBar.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);
        recyclerSesiones.setVisibility(View.GONE);

        ApiClient.getAgendaService()
                .listarPorSemana(fechaApi)
                .enqueue(new Callback<List<SesionEntrenamiento>>() {
                    @Override
                    public void onResponse(Call<List<SesionEntrenamiento>> call,
                                           Response<List<SesionEntrenamiento>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            List<SesionEntrenamiento> lista = response.body();
                            adapter.setSesiones(lista);
                            recyclerSesiones.setVisibility(lista.isEmpty() ? View.GONE : View.VISIBLE);
                            tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
                        } else {
                            tvVacio.setVisibility(View.VISIBLE);
                            Toast.makeText(AgendaActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SesionEntrenamiento>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvVacio.setVisibility(View.VISIBLE);
                        Toast.makeText(AgendaActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_agenda);
        cargarSesiones();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_agenda) return true;
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, DashboardActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            } else if (id == R.id.nav_marcas) {
                startActivity(new Intent(this, MarcasActivity.class)
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
        nav.setSelectedItemId(R.id.nav_agenda);
    }
}
