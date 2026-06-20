package com.example.tallerappmovil.perfil;

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
import com.example.tallerappmovil.auth.LoginActivity;
import com.example.tallerappmovil.dashboard.DashboardActivity;
import com.example.tallerappmovil.eventos.EventosActivity;
import com.example.tallerappmovil.marcas.MarcasActivity;
import com.example.tallerappmovil.model.PerfilUsuario;
import com.example.tallerappmovil.session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilActivity extends AppCompatActivity {

    private static final int REQ_EDITAR = 100;

    private TextView tvAvatar, tvNombre, tvRol, tvEmail;
    private ImageView ivAvatar;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        session   = new SessionManager(this);
        String nombre = session.getUserName();
        String rol    = session.getUserRole();

        tvAvatar = findViewById(R.id.tvAvatar);
        ivAvatar = findViewById(R.id.ivAvatar);
        tvNombre = findViewById(R.id.tvNombre);
        tvRol    = findViewById(R.id.tvRol);
        tvEmail  = findViewById(R.id.tvEmail);

        mostrarDatosLocales(nombre, rol);
        String emailCached = session.getUserEmail();
        if (!emailCached.isEmpty()) tvEmail.setText(emailCached);
        cargarPerfilApi();

        // Botón editar perfil (outline bajo el avatar)
        findViewById(R.id.btnEditarPerfil).setOnClickListener(v ->
                startActivityForResult(new Intent(this, EditarPerfilActivity.class), REQ_EDITAR));

        // Accesos rápidos
        findViewById(R.id.cardMiAsistencia).setOnClickListener(v ->
                startActivity(new Intent(this, HistorialAsistenciaActivity.class)));

        findViewById(R.id.cardMisMarcas).setOnClickListener(v ->
                startActivity(new Intent(this, MarcasActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)));

        findViewById(R.id.cardCambiarContrasena).setOnClickListener(v ->
                startActivity(new Intent(this, CambiarContrasenaActivity.class)));

        MaterialButton btnLogout = findViewById(R.id.btnCerrarSesion);
        btnLogout.setOnClickListener(v -> {
            session.clearSession();
            ApiClient.clearToken();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        setupBottomNav();
    }

    private void mostrarDatosLocales(String nombre, String rol) {
        if (!nombre.isEmpty()) {
            tvAvatar.setText(String.valueOf(nombre.charAt(0)).toUpperCase());
        }
        tvNombre.setText(nombre);
        tvRol.setText(rolLabel(rol));
    }

    private void cargarPerfilApi() {
        ApiClient.getUsuariosService().getPerfil()
                .enqueue(new Callback<PerfilUsuario>() {
                    @Override
                    public void onResponse(Call<PerfilUsuario> call, Response<PerfilUsuario> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            PerfilUsuario p = response.body();
                            String nombre = p.getNombreCompleto();
                            session.saveUserName(nombre);
                            if (p.getEmail() != null) {
                                session.saveUserEmail(p.getEmail());
                                tvEmail.setText(p.getEmail());
                            }
                            session.saveGrupo(p.getGrupoId(), p.getGrupoNombre());
                            mostrarDatosLocales(nombre, session.getUserRole());
                            cargarFoto(p.getFotoUrl());
                        }
                    }
                    @Override public void onFailure(Call<PerfilUsuario> call, Throwable t) {}
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_EDITAR && resultCode == RESULT_OK) {
            // Recargar datos actualizados
            mostrarDatosLocales(session.getUserName(), session.getUserRole());
            cargarPerfilApi();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_perfil);
    }

    private void cargarFoto(String url) {
        if (url == null || url.isEmpty()) return;
        ivAvatar.setVisibility(View.VISIBLE);
        tvAvatar.setVisibility(View.GONE);
        Glide.with(this).load(url).transform(new CircleCrop()).into(ivAvatar);
    }

    private String rolLabel(String rol) {
        if ("ENTRENADOR".equals(rol)) return "Entrenador";
        if ("ADMIN".equals(rol))      return "Administrador";
        if ("ATLETA".equals(rol))     return "Atleta";
        return rol;
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_perfil) return true;
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, DashboardActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            } else if (id == R.id.nav_agenda) {
                startActivity(new Intent(this, AgendaActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            } else if (id == R.id.nav_marcas) {
                startActivity(new Intent(this, MarcasActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            } else if (id == R.id.nav_eventos) {
                startActivity(new Intent(this, EventosActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            }
            overridePendingTransition(0, 0);
            return false;
        });
        nav.setSelectedItemId(R.id.nav_perfil);
    }
}
