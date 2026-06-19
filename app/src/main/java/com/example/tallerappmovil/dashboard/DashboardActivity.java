package com.example.tallerappmovil.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.agenda.AgendaActivity;
import com.example.tallerappmovil.asistencia.AsistenciaActivity;
import com.example.tallerappmovil.atletas.AtletasActivity;
import com.example.tallerappmovil.grupos.GruposActivity;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.eventos.EventosActivity;
import com.example.tallerappmovil.marcas.MarcasActivity;
import com.example.tallerappmovil.model.AtletaInfo;
import com.example.tallerappmovil.model.Competencia;
import com.example.tallerappmovil.model.MarcaPersonal;
import com.example.tallerappmovil.model.Notificacion;
import com.example.tallerappmovil.model.SesionEntrenamiento;
import com.example.tallerappmovil.asistencia.ReporteAsistenciaActivity;
import com.example.tallerappmovil.notificaciones.NotificacionesActivity;
import com.example.tallerappmovil.perfil.PerfilActivity;
import com.example.tallerappmovil.session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvSesionNombre, tvSesionDetalle, tvSesionEstado;
    private TextView tvAtletas, tvNuevasMarcas, tvProxCompetencia;
    private TextView tvBadgeNotif;
    private View layoutBotonesSesion;
    private SesionEntrenamiento sesionHoy;
    private boolean esEntrenador;

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        SessionManager session = new SessionManager(this);
        String nombre = session.getUserName();
        String rol    = session.getUserRole();
        esEntrenador  = "ENTRENADOR".equals(rol) || "ADMIN".equals(rol);

        TextView tvSaludo    = findViewById(R.id.tvSaludo);
        TextView tvSubtitulo = findViewById(R.id.tvSubtitulo);
        TextView tvAvatar    = findViewById(R.id.tvAvatar);

        String firstName = nombre.contains(" ") ? nombre.split(" ")[0] : nombre;
        tvSaludo.setText("Hola, " + firstName + " 👋");
        tvSubtitulo.setText(esEntrenador ? "Panel del Entrenador" : "Club Atlético Santa Cruz");
        if (!nombre.isEmpty()) {
            tvAvatar.setText(String.valueOf(nombre.charAt(0)).toUpperCase());
        }

        tvSesionNombre      = findViewById(R.id.tvSesionNombre);
        tvSesionDetalle     = findViewById(R.id.tvSesionDetalle);
        tvSesionEstado      = findViewById(R.id.tvSesionEstado);
        tvAtletas           = findViewById(R.id.tvAtletas);
        tvNuevasMarcas      = findViewById(R.id.tvNuevasMarcas);
        tvProxCompetencia   = findViewById(R.id.tvProxCompetencia);
        tvBadgeNotif        = findViewById(R.id.tvBadgeNotif);
        layoutBotonesSesion = findViewById(R.id.layoutBotonesSesion);

        // Botón notificaciones → NotificacionesActivity
        findViewById(R.id.btnNotificaciones).setOnClickListener(v ->
                startActivity(new Intent(this, NotificacionesActivity.class)));

        // Card: atletas → AtletasActivity (solo entrenador)
        View cardAtletas = findViewById(R.id.cardAtletas);
        View layoutAccesosEntrenador = findViewById(R.id.layoutAccesosEntrenador);
        if (esEntrenador) {
            cardAtletas.setClickable(true);
            cardAtletas.setFocusable(true);
            cardAtletas.setForeground(getDrawable(android.R.attr.selectableItemBackground));
            cardAtletas.setOnClickListener(v ->
                    startActivity(new Intent(this, AtletasActivity.class)));

            layoutAccesosEntrenador.setVisibility(View.VISIBLE);
            findViewById(R.id.cardGrupos).setOnClickListener(v ->
                    startActivity(new Intent(this, GruposActivity.class)));
            findViewById(R.id.cardReporte).setOnClickListener(v ->
                    startActivity(new Intent(this, ReporteAsistenciaActivity.class)));
        }

        // Card: marcas → MarcasActivity
        findViewById(R.id.cardNuevasMarcas).setOnClickListener(v ->
                startActivity(new Intent(this, MarcasActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)));

        // Card: competencias → EventosActivity
        findViewById(R.id.cardProxCompetencia).setOnClickListener(v ->
                startActivity(new Intent(this, EventosActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)));

        // Card: asistencia → AgendaActivity
        findViewById(R.id.cardAsistenciaHoy).setOnClickListener(v ->
                startActivity(new Intent(this, AgendaActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)));

        findViewById(R.id.btnVerAgenda).setOnClickListener(v -> {
            startActivity(new Intent(this, AgendaActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            overridePendingTransition(0, 0);
        });

        findViewById(R.id.btnTomarAsistencia).setOnClickListener(v -> {
            if (sesionHoy == null) return;
            Intent intent = new Intent(this, AsistenciaActivity.class);
            intent.putExtra(AsistenciaActivity.EXTRA_SESION_ID, sesionHoy.getId());
            intent.putExtra(AsistenciaActivity.EXTRA_GRUPO, sesionHoy.getGrupoNombre());
            intent.putExtra(AsistenciaActivity.EXTRA_HORA, sesionHoy.getHoraInicio());
            intent.putExtra(AsistenciaActivity.EXTRA_LUGAR, sesionHoy.getLugar());
            startActivity(intent);
        });

        setupBottomNav();
        loadTodaySesion();
        loadStats();
        loadNotifBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_inicio);
        loadStats();
        loadNotifBadge();
    }

    private void loadStats() {
        // Cuenta de atletas (solo entrenador)
        if (esEntrenador) {
            ApiClient.getAtletasService().getAtletas()
                    .enqueue(new Callback<List<AtletaInfo>>() {
                        @Override
                        public void onResponse(Call<List<AtletaInfo>> call,
                                               Response<List<AtletaInfo>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                tvAtletas.setText(String.valueOf(response.body().size()));
                            }
                        }
                        @Override public void onFailure(Call<List<AtletaInfo>> call, Throwable t) {}
                    });
        }

        // Próxima competencia
        ApiClient.getCompetenciasService().getCompetencias("PROXIMO")
                .enqueue(new Callback<List<Competencia>>() {
                    @Override
                    public void onResponse(Call<List<Competencia>> call,
                                           Response<List<Competencia>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && !response.body().isEmpty()) {
                            Competencia prox = response.body().get(0);
                            String fecha = prox.getFechaEvento();
                            if (fecha != null && fecha.length() >= 10) {
                                // Muestra "DD/MM"
                                tvProxCompetencia.setText(
                                        fecha.substring(8, 10) + "/" + fecha.substring(5, 7));
                            } else {
                                tvProxCompetencia.setText(prox.getNombre() != null
                                        ? prox.getNombre().substring(0, Math.min(6, prox.getNombre().length()))
                                        : "--");
                            }
                        }
                    }
                    @Override public void onFailure(Call<List<Competencia>> call, Throwable t) {}
                });

        // Nuevas marcas (total del atleta)
        ApiClient.getMarcasService().getMarcas(null)
                .enqueue(new Callback<List<MarcaPersonal>>() {
                    @Override
                    public void onResponse(Call<List<MarcaPersonal>> call,
                                           Response<List<MarcaPersonal>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            tvNuevasMarcas.setText(String.valueOf(response.body().size()));
                        }
                    }
                    @Override public void onFailure(Call<List<MarcaPersonal>> call, Throwable t) {}
                });
    }

    private void loadNotifBadge() {
        ApiClient.getNotificacionesService().getNotificaciones()
                .enqueue(new Callback<List<Notificacion>>() {
                    @Override
                    public void onResponse(Call<List<Notificacion>> call,
                                           Response<List<Notificacion>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            long noLeidas = 0;
                            for (Notificacion n : response.body()) if (!n.isLeida()) noLeidas++;
                            if (noLeidas > 0) {
                                tvBadgeNotif.setVisibility(View.VISIBLE);
                                tvBadgeNotif.setText(noLeidas > 9 ? "9+" : String.valueOf(noLeidas));
                            } else {
                                tvBadgeNotif.setVisibility(View.GONE);
                            }
                        }
                    }
                    @Override public void onFailure(Call<List<Notificacion>> call, Throwable t) {}
                });
    }

    private void loadTodaySesion() {
        String today = DATE_FORMAT.format(new Date());
        ApiClient.getAgendaService().listarPorSemana(today)
                .enqueue(new Callback<List<SesionEntrenamiento>>() {
                    @Override
                    public void onResponse(Call<List<SesionEntrenamiento>> call,
                                           Response<List<SesionEntrenamiento>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            for (SesionEntrenamiento s : response.body()) {
                                if (s.getHoraInicio() != null && s.getHoraInicio().startsWith(today)) {
                                    showSesion(s);
                                    break;
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<List<SesionEntrenamiento>> call, Throwable t) {}
                });
    }

    private void showSesion(SesionEntrenamiento sesion) {
        sesionHoy = sesion;
        String grupo = sesion.getGrupoNombre() != null ? sesion.getGrupoNombre() : "Entrenamiento";
        String lugar = sesion.getLugar() != null ? " · " + sesion.getLugar() : "";
        tvSesionNombre.setText(grupo + lugar);

        tvSesionDetalle.setText(formatHora(sesion.getHoraInicio()) + " – " + formatHora(sesion.getHoraFin()));
        tvSesionDetalle.setVisibility(View.VISIBLE);

        String estado = sesion.getEstado() != null ? sesion.getEstado() : "PROGRAMADA";
        switch (estado) {
            case "ACTIVA":
                tvSesionEstado.setText("Activa");
                tvSesionEstado.setBackgroundColor(getColor(R.color.colorActive));
                tvSesionEstado.setVisibility(View.VISIBLE);
                break;
            case "CANCELADA":
                tvSesionEstado.setText("Cancelada");
                tvSesionEstado.setBackgroundColor(getColor(R.color.colorCancelled));
                tvSesionEstado.setVisibility(View.VISIBLE);
                break;
            case "PROGRAMADA":
                tvSesionEstado.setText("Programada");
                tvSesionEstado.setBackgroundColor(getColor(R.color.colorPrimary));
                tvSesionEstado.setVisibility(View.VISIBLE);
                break;
        }
        layoutBotonesSesion.setVisibility(View.VISIBLE);
        findViewById(R.id.btnTomarAsistencia).setVisibility(esEntrenador ? View.VISIBLE : View.GONE);
    }

    private String formatHora(String iso) {
        if (iso == null) return "--";
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(iso);
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(d);
        } catch (Exception e) {
            return iso;
        }
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) return true;
            if (id == R.id.nav_agenda) {
                startActivity(new Intent(this, AgendaActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                overridePendingTransition(0, 0);
            } else if (id == R.id.nav_marcas) {
                startActivity(new Intent(this, MarcasActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                overridePendingTransition(0, 0);
            } else if (id == R.id.nav_eventos) {
                startActivity(new Intent(this, EventosActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                overridePendingTransition(0, 0);
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                overridePendingTransition(0, 0);
            }
            return false;
        });
        nav.setSelectedItemId(R.id.nav_inicio);
    }
}
