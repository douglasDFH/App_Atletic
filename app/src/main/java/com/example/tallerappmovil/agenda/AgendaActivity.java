package com.example.tallerappmovil.agenda;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.tallerappmovil.dashboard.DashboardActivity;
import com.example.tallerappmovil.eventos.EventosActivity;
import com.example.tallerappmovil.marcas.MarcasActivity;
import com.example.tallerappmovil.model.SesionEntrenamiento;
import com.example.tallerappmovil.perfil.PerfilActivity;
import com.example.tallerappmovil.session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

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
    private TextView tvSemana, tvVacio, tvContadorSesiones;
    private ProgressBar progressBar;
    private TextInputEditText etBuscar;
    private ChipGroup chipGroupEstado;

    private final Calendar semanaActual = Calendar.getInstance();
    private String estadoFiltro  = null;
    private boolean soloMiGrupo  = false;
    private Long miGrupoId       = null;
    private String miGrupoNombre = null;

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

        SessionManager session = new SessionManager(this);
        String userRol = session.getUserRole();
        boolean puedeEditar = "ENTRENADOR".equals(userRol) || "ADMIN".equals(userRol);
        boolean esAtleta = "ATLETA".equals(userRol) || "PADRE".equals(userRol);

        miGrupoId     = session.getGrupoId();
        miGrupoNombre = session.getGrupoNombre();

        recyclerSesiones  = findViewById(R.id.recyclerSesiones);
        tvSemana          = findViewById(R.id.tvSemana);
        tvVacio           = findViewById(R.id.tvVacio);
        progressBar       = findViewById(R.id.progressBar);
        tvContadorSesiones = findViewById(R.id.tvContadorSesiones);
        etBuscar          = findViewById(R.id.etBuscar);
        chipGroupEstado   = findViewById(R.id.chipGroupEstado);

        FloatingActionButton fab = findViewById(R.id.fabCrear);
        fab.setVisibility(puedeEditar ? View.VISIBLE : View.GONE);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearSesionActivity.class);
            // Pasar la semana visible para que la sesión nueva quede en esa semana
            intent.putExtra(CrearSesionActivity.EXTRA_SEMANA_DEFAULT,
                    API_FORMAT.format(semanaActual.getTime()));
            startActivity(intent);
        });

        recyclerSesiones.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SesionAdapter(puedeEditar, sesion -> {
            Intent intent = new Intent(this, SesionDetalleActivity.class);
            intent.putExtra(SesionDetalleActivity.EXTRA_SESION_ID,   sesion.getId());
            intent.putExtra(SesionDetalleActivity.EXTRA_GRUPO,        sesion.getGrupoNombre());
            intent.putExtra(SesionDetalleActivity.EXTRA_HORA_INICIO,  sesion.getHoraInicio());
            intent.putExtra(SesionDetalleActivity.EXTRA_HORA_FIN,     sesion.getHoraFin());
            intent.putExtra(SesionDetalleActivity.EXTRA_LUGAR,        sesion.getLugar());
            intent.putExtra(SesionDetalleActivity.EXTRA_DESCRIPCION,  sesion.getDescripcion());
            intent.putExtra(SesionDetalleActivity.EXTRA_ESTADO,       sesion.getEstado());
            intent.putExtra(SesionDetalleActivity.EXTRA_MOTIVO,       sesion.getMotivoCancelacion());
            intent.putExtra(SesionDetalleActivity.EXTRA_GRUPO_ID,
                    sesion.getGrupoId() != null ? sesion.getGrupoId() : -1L);
            startActivity(intent);
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

        // Mostrar chip "Mi grupo" solo si es atleta con grupo asignado
        if (esAtleta && miGrupoId != null) {
            com.google.android.material.chip.Chip chipMiGrupo = findViewById(R.id.chipMiGrupo);
            String label = miGrupoNombre != null && !miGrupoNombre.isEmpty()
                    ? miGrupoNombre : "Mi grupo";
            chipMiGrupo.setText(label);
            chipMiGrupo.setVisibility(android.view.View.VISIBLE);
        }

        setupChips();
        setupBusqueda();
        setupBottomNav();
    }

    private void moverALunes(Calendar cal) {
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DATE, -1);
        }
    }

    private void setupChips() {
        chipGroupEstado.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipTodos) {
                estadoFiltro = null;
                soloMiGrupo  = false;
            } else if (id == R.id.chipMiGrupo) {
                estadoFiltro = null;
                soloMiGrupo  = true;
            } else if (id == R.id.chipProgramadas) {
                estadoFiltro = "PROGRAMADA";
                soloMiGrupo  = false;
            } else if (id == R.id.chipActivas) {
                estadoFiltro = "ACTIVA";
                soloMiGrupo  = false;
            } else if (id == R.id.chipFinalizadas) {
                estadoFiltro = "FINALIZADA";
                soloMiGrupo  = false;
            } else if (id == R.id.chipCanceladas) {
                estadoFiltro = "CANCELADA";
                soloMiGrupo  = false;
            }
            aplicarFiltros();
        });
    }

    private void setupBusqueda() {
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int i, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) {
                aplicarFiltros();
            }
        });
    }

    private void aplicarFiltros() {
        String query = etBuscar.getText() != null ? etBuscar.getText().toString().trim() : "";
        Long grupoFiltro = soloMiGrupo ? miGrupoId : null;
        int visible = adapter.filtrar(query, estadoFiltro, grupoFiltro);
        recyclerSesiones.setVisibility(visible > 0 ? View.VISIBLE : View.GONE);
        actualizarContador(visible);
        tvVacio.setVisibility(visible == 0 ? View.VISIBLE : View.GONE);
        Log.d("AgendaFiltros", "visible=" + visible + " total=" + adapter.getTotalCount()
                + " estado=" + estadoFiltro + " soloMiGrupo=" + soloMiGrupo
                + " rv=" + recyclerSesiones.getVisibility());
        Toast.makeText(this, "Items: " + visible + "/" + adapter.getTotalCount(),
                Toast.LENGTH_SHORT).show();
    }

    private void actualizarContador(int visible) {
        int total = adapter.getTotalCount();
        if (total == 0) {
            tvContadorSesiones.setVisibility(View.GONE);
            return;
        }
        String texto = visible == total
                ? visible + " sesión" + (visible != 1 ? "es" : "") + " esta semana"
                : visible + " de " + total + " sesiones";
        tvContadorSesiones.setText(texto);
        tvContadorSesiones.setVisibility(View.VISIBLE);
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
        tvContadorSesiones.setVisibility(View.GONE);
        recyclerSesiones.setVisibility(View.GONE);

        ApiClient.getAgendaService()
                .listarPorSemana(fechaApi)
                .enqueue(new Callback<List<SesionEntrenamiento>>() {
                    @Override
                    public void onResponse(Call<List<SesionEntrenamiento>> call,
                                           Response<List<SesionEntrenamiento>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            int count = response.body().size();
                            Log.d("AgendaActivity", "cargarSesiones semana=" + fechaApi
                                    + " → " + count + " sesiones");
                            Toast.makeText(AgendaActivity.this,
                                    "Semana " + fechaApi + ": " + count + " sesión(es)",
                                    Toast.LENGTH_SHORT).show();
                            adapter.setSesiones(response.body());
                            aplicarFiltros();
                        } else {
                            Log.e("AgendaActivity", "cargarSesiones HTTP " + response.code()
                                    + " semana=" + fechaApi);
                            tvVacio.setVisibility(View.VISIBLE);
                            Toast.makeText(AgendaActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<SesionEntrenamiento>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvVacio.setVisibility(View.VISIBLE);
                        Log.e("AgendaActivity", "cargarSesiones onFailure semana=" + fechaApi
                                + " → " + t.getClass().getSimpleName(), t);
                        Toast.makeText(AgendaActivity.this,
                                "Error carga: " + t.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
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
