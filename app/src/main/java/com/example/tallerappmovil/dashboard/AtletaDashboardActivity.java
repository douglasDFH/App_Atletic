package com.example.tallerappmovil.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.tallerappmovil.R;
import com.example.tallerappmovil.agenda.AgendaActivity;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.asistencia.HistorialAsistenciaActivity;
import com.example.tallerappmovil.eventos.EventosActivity;
import com.example.tallerappmovil.marcas.EvolucionMarcasActivity;
import com.example.tallerappmovil.marcas.MarcasActivity;
import com.example.tallerappmovil.model.Competencia;
import com.example.tallerappmovil.model.Notificacion;
import com.example.tallerappmovil.model.PerfilUsuario;
import com.example.tallerappmovil.model.SesionEntrenamiento;
import com.example.tallerappmovil.notificaciones.NotificacionesActivity;
import com.example.tallerappmovil.perfil.PerfilActivity;
import com.example.tallerappmovil.ranking.RankingActivity;
import com.example.tallerappmovil.session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AtletaDashboardActivity extends AppCompatActivity {

    private TextView tvSaludo;
    private TextView tvSubtituloAtleta;
    private TextView tvAvatar;
    private ImageView ivAvatar;
    private TextView tvBadgeNotif;
    private TextView tvProxSesion;
    private TextView tvProxCompetencia;
    private View bannerPadre;
    private TextView tvHijoNombreBanner;

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atleta_dashboard);

        SessionManager session = new SessionManager(this);

        tvSaludo            = findViewById(R.id.tvSaludo);
        tvSubtituloAtleta   = findViewById(R.id.tvSubtituloAtleta);
        tvAvatar            = findViewById(R.id.tvAvatar);
        ivAvatar            = findViewById(R.id.ivAvatar);
        tvBadgeNotif        = findViewById(R.id.tvBadgeNotif);
        tvProxSesion        = findViewById(R.id.tvProxSesion);
        tvProxCompetencia   = findViewById(R.id.tvProxCompetencia);
        bannerPadre         = findViewById(R.id.bannerPadre);
        tvHijoNombreBanner  = findViewById(R.id.tvHijoNombreBanner);

        actualizarSaludo(session.getUserName());

        // Mostrar banner PADRE desde caché inmediatamente si aplica
        if ("PADRE".equals(session.getUserRole())) {
            String hijoCache = session.getHijoNombre();
            if (hijoCache != null) mostrarBannerPadre(hijoCache);
            else tvSubtituloAtleta.setText("Modo padre");
        }

        // Foto desde caché inmediatamente
        String cachedFoto = session.getFotoUrl();
        if (cachedFoto != null) mostrarFotoAvatar(cachedFoto);

        // Avatar → perfil
        findViewById(R.id.avatarCircle).setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)));

        // Campanita → notificaciones
        findViewById(R.id.btnNotificaciones).setOnClickListener(v ->
                startActivity(new Intent(this, NotificacionesActivity.class)));

        // Cards de módulos
        findViewById(R.id.cardAgenda).setOnClickListener(v ->
                startActivity(new Intent(this, AgendaActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)));

        findViewById(R.id.cardMarcas).setOnClickListener(v ->
                startActivity(new Intent(this, MarcasActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)));

        findViewById(R.id.cardEvolucion).setOnClickListener(v ->
                startActivity(new Intent(this, EvolucionMarcasActivity.class)));

        findViewById(R.id.cardCompetencias).setOnClickListener(v ->
                startActivity(new Intent(this, EventosActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)));

        findViewById(R.id.cardRanking).setOnClickListener(v ->
                startActivity(new Intent(this, RankingActivity.class)));

        findViewById(R.id.cardAsistencia).setOnClickListener(v ->
                startActivity(new Intent(this, HistorialAsistenciaActivity.class)));

        // Stats cards
        findViewById(R.id.cardProxSesion).setOnClickListener(v ->
                startActivity(new Intent(this, AgendaActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)));

        findViewById(R.id.cardProxCompetencia).setOnClickListener(v ->
                startActivity(new Intent(this, EventosActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)));

        setupBottomNav();
        cargarFotoAvatar();
        cargarStats();
        loadNotifBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_inicio);
        SessionManager session = new SessionManager(this);
        actualizarSaludo(session.getUserName());
        String cachedFoto = session.getFotoUrl();
        if (cachedFoto != null) mostrarFotoAvatar(cachedFoto);
        loadNotifBadge();
    }

    private void actualizarSaludo(String nombre) {
        if (nombre == null || nombre.isEmpty()) return;
        String firstName = nombre.contains(" ") ? nombre.split(" ")[0] : nombre;
        tvSaludo.setText("Hola, " + firstName + " 👋");
        tvAvatar.setText(String.valueOf(nombre.charAt(0)).toUpperCase());
    }

    private void cargarFotoAvatar() {
        ApiClient.getUsuariosService().getPerfil()
                .enqueue(new Callback<PerfilUsuario>() {
                    @Override
                    public void onResponse(Call<PerfilUsuario> call, Response<PerfilUsuario> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            PerfilUsuario p = response.body();
                            SessionManager sm = new SessionManager(AtletaDashboardActivity.this);
                            sm.saveFotoUrl(p.getFotoUrl());
                            // Cachear el grupo del atleta para que la Agenda pueda filtrar "Mi grupo"
                            // (para un PADRE, el backend devuelve el grupo del hijo vinculado)
                            sm.saveGrupo(p.getGrupoId(), p.getGrupoNombre());
                            // Si es PADRE, guardar el hijo en caché y mostrar banner
                            if ("PADRE".equals(sm.getUserRole()) && p.getAtletaVinculadoNombre() != null) {
                                sm.saveHijo(p.getAtletaVinculadoId(), p.getAtletaVinculadoNombre());
                                mostrarBannerPadre(p.getAtletaVinculadoNombre());
                            }
                            if (p.getNombreCompleto() != null) {
                                sm.saveUserName(p.getNombreCompleto());
                                actualizarSaludo(p.getNombreCompleto());
                            }
                            mostrarFotoAvatar(p.getFotoUrl());
                        }
                    }
                    @Override public void onFailure(Call<PerfilUsuario> call, Throwable t) {}
                });
    }

    private void mostrarBannerPadre(String hijoNombre) {
        tvSubtituloAtleta.setText("Modo padre");
        tvHijoNombreBanner.setText(hijoNombre);
        bannerPadre.setVisibility(View.VISIBLE);
    }

    private void mostrarFotoAvatar(String url) {
        String fullUrl = ApiClient.resolveUrl(url);
        if (fullUrl == null || ivAvatar == null) return;
        ivAvatar.setVisibility(View.VISIBLE);
        tvAvatar.setVisibility(View.GONE);
        Glide.with(this).load(fullUrl).transform(new CircleCrop()).into(ivAvatar);
    }

    private void loadNotifBadge() {
        ApiClient.getNotificacionesService().getNotificaciones()
                .enqueue(new Callback<List<Notificacion>>() {
                    @Override
                    public void onResponse(Call<List<Notificacion>> call, Response<List<Notificacion>> response) {
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

    private void cargarStats() {
        String today = DATE_FORMAT.format(new Date());

        // Próxima sesión
        ApiClient.getAgendaService().listarPorSemana(today)
                .enqueue(new Callback<List<SesionEntrenamiento>>() {
                    @Override
                    public void onResponse(Call<List<SesionEntrenamiento>> call,
                                           Response<List<SesionEntrenamiento>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && !response.body().isEmpty()) {
                            SesionEntrenamiento s = response.body().get(0);
                            String hora = s.getHoraInicio();
                            if (hora != null && hora.length() >= 16) {
                                tvProxSesion.setText(hora.substring(11, 16));
                            } else {
                                tvProxSesion.setText("Hoy");
                            }
                        } else {
                            tvProxSesion.setText("Sin sesiones");
                        }
                    }
                    @Override public void onFailure(Call<List<SesionEntrenamiento>> call, Throwable t) {
                        tvProxSesion.setText("--");
                    }
                });

        // Próxima competencia
        ApiClient.getCompetenciasService().getCompetencias("PROXIMO")
                .enqueue(new Callback<List<Competencia>>() {
                    @Override
                    public void onResponse(Call<List<Competencia>> call,
                                           Response<List<Competencia>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && !response.body().isEmpty()) {
                            String fecha = response.body().get(0).getFechaEvento();
                            if (fecha != null && fecha.length() >= 10) {
                                tvProxCompetencia.setText(
                                        fecha.substring(8, 10) + "/" + fecha.substring(5, 7));
                            } else {
                                tvProxCompetencia.setText("Pronto");
                            }
                        } else {
                            tvProxCompetencia.setText("--");
                        }
                    }
                    @Override public void onFailure(Call<List<Competencia>> call, Throwable t) {
                        tvProxCompetencia.setText("--");
                    }
                });
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
