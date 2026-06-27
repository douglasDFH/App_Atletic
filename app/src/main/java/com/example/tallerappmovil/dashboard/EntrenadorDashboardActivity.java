package com.example.tallerappmovil.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.agenda.AgendaActivity;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.asistencia.AsistenciaActivity;
import com.example.tallerappmovil.atletas.AtletasActivity;
import com.example.tallerappmovil.auth.LoginActivity;
import com.example.tallerappmovil.disciplinas.DisciplinasActivity;
import com.example.tallerappmovil.estadisticas.EstadisticasActivity;
import com.example.tallerappmovil.eventos.EventosActivity;
import com.example.tallerappmovil.marcas.MarcasActivity;
import com.example.tallerappmovil.session.SessionManager;

public class EntrenadorDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrenador_dashboard);

        SessionManager session = new SessionManager(this);
        String nombre = session.getUserName();

        TextView tvBienvenido = findViewById(R.id.tvBienvenido);
        tvBienvenido.setText(getString(R.string.lbl_bienvenido_usuario, nombre));

        TextView tvAvatar = findViewById(R.id.tvAvatar);
        if (!nombre.isEmpty()) tvAvatar.setText(String.valueOf(nombre.charAt(0)).toUpperCase());

        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> confirmarCerrarSesion(session));

        findViewById(R.id.cardAgenda).setOnClickListener(v ->
                startActivity(new Intent(this, AgendaActivity.class)));

        findViewById(R.id.cardAsistencia).setOnClickListener(v ->
                startActivity(new Intent(this, AsistenciaActivity.class)));

        findViewById(R.id.cardRendimiento).setOnClickListener(v ->
                startActivity(new Intent(this, MarcasActivity.class)));

        findViewById(R.id.cardAtletas).setOnClickListener(v ->
                startActivity(new Intent(this, AtletasActivity.class)));

        findViewById(R.id.cardDisciplinas).setOnClickListener(v ->
                startActivity(new Intent(this, DisciplinasActivity.class)));

        findViewById(R.id.cardEstadisticas).setOnClickListener(v ->
                startActivity(new Intent(this, EstadisticasActivity.class)));

        findViewById(R.id.cardCompetencias).setOnClickListener(v ->
                startActivity(new Intent(this, EventosActivity.class)));
    }

    private void confirmarCerrarSesion(SessionManager session) {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Deseas cerrar tu sesión?")
                .setPositiveButton("Sí", (d, w) -> {
                    session.clearSession();
                    ApiClient.clearToken();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
