package com.example.tallerappmovil.agenda;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.asistencia.AsistenciaActivity;
import com.example.tallerappmovil.model.AsistenciaAtleta;
import com.example.tallerappmovil.session.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SesionDetalleActivity extends AppCompatActivity {

    public static final String EXTRA_SESION_ID    = "sesion_id";
    public static final String EXTRA_GRUPO        = "grupo";
    public static final String EXTRA_HORA_INICIO  = "hora_inicio";
    public static final String EXTRA_HORA_FIN     = "hora_fin";
    public static final String EXTRA_LUGAR        = "lugar";
    public static final String EXTRA_DESCRIPCION  = "descripcion";
    public static final String EXTRA_ESTADO       = "estado";
    public static final String EXTRA_MOTIVO       = "motivo";
    public static final String EXTRA_GRUPO_ID     = "grupo_id";

    private long sesionId;
    private boolean esEntrenador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sesion_detalle);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String rol = new SessionManager(this).getUserRole();
        esEntrenador = "ENTRENADOR".equals(rol) || "ADMIN".equals(rol);

        sesionId             = getIntent().getLongExtra(EXTRA_SESION_ID, -1L);
        String grupo         = getIntent().getStringExtra(EXTRA_GRUPO);
        String horaInicio    = getIntent().getStringExtra(EXTRA_HORA_INICIO);
        String horaFin       = getIntent().getStringExtra(EXTRA_HORA_FIN);
        String lugar         = getIntent().getStringExtra(EXTRA_LUGAR);
        String descripcion   = getIntent().getStringExtra(EXTRA_DESCRIPCION);
        String estado        = getIntent().getStringExtra(EXTRA_ESTADO);
        String motivo        = getIntent().getStringExtra(EXTRA_MOTIVO);
        long grupoId         = getIntent().getLongExtra(EXTRA_GRUPO_ID, -1L);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(grupo != null ? grupo : getString(R.string.lbl_detalle_sesion));

        // Grupo + estado badge
        TextView tvGrupo = findViewById(R.id.tvGrupo);
        tvGrupo.setText(grupo != null ? grupo : "—");

        TextView tvEstadoBadge = findViewById(R.id.tvEstadoBadge);
        if (estado != null) {
            tvEstadoBadge.setText(estadoLabel(estado));
            tvEstadoBadge.setBackgroundTintList(ColorStateList.valueOf(estadoColor(estado)));
            tvEstadoBadge.setVisibility(View.VISIBLE);
        }

        // Fecha y horario
        TextView tvFechaHora = findViewById(R.id.tvFechaHora);
        tvFechaHora.setText(formatFechaHora(horaInicio, horaFin));

        // Lugar
        if (lugar != null && !lugar.isEmpty()) {
            ((TextView) findViewById(R.id.tvLugar)).setText(lugar);
            findViewById(R.id.rowLugar).setVisibility(View.VISIBLE);
        }

        // Descripción
        if (descripcion != null && !descripcion.isEmpty()) {
            TextView tvDesc = findViewById(R.id.tvDescripcion);
            tvDesc.setText(descripcion);
            tvDesc.setVisibility(View.VISIBLE);
        }

        // Motivo cancelación
        if (motivo != null && !motivo.isEmpty()) {
            ((TextView) findViewById(R.id.tvMotivo)).setText(motivo);
            findViewById(R.id.rowMotivo).setVisibility(View.VISIBLE);
        }

        if (esEntrenador) {
            configurarVistaEntrenador(grupoId, horaInicio, lugar, grupo);
        }

        if (sesionId != -1L) {
            cargarAsistencia(sesionId);
        }
    }

    private void configurarVistaEntrenador(long grupoId, String horaInicio, String lugar, String grupo) {
        View layoutBotones = findViewById(R.id.layoutBotonesEntrenador);
        layoutBotones.setVisibility(View.VISIBLE);
        findViewById(R.id.layoutResumenAsistencia).setVisibility(View.VISIBLE);
        ((android.widget.ProgressBar) findViewById(R.id.progressAsistencia)).setVisibility(View.VISIBLE);

        MaterialButton btnAsistencia = findViewById(R.id.btnTomarAsistencia);
        MaterialButton btnEditar     = findViewById(R.id.btnEditarSesion);

        btnAsistencia.setOnClickListener(v -> {
            Intent intent = new Intent(this, AsistenciaActivity.class);
            intent.putExtra(AsistenciaActivity.EXTRA_SESION_ID, sesionId);
            intent.putExtra(AsistenciaActivity.EXTRA_GRUPO, grupo);
            intent.putExtra(AsistenciaActivity.EXTRA_HORA, horaInicio);
            intent.putExtra(AsistenciaActivity.EXTRA_LUGAR, lugar);
            startActivity(intent);
        });

        btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearSesionActivity.class);
            intent.putExtra(CrearSesionActivity.EXTRA_SESION_ID, sesionId);
            intent.putExtra(CrearSesionActivity.EXTRA_HORA_INICIO, horaInicio);
            intent.putExtra(CrearSesionActivity.EXTRA_HORA_FIN, getIntent().getStringExtra(EXTRA_HORA_FIN));
            intent.putExtra(CrearSesionActivity.EXTRA_LUGAR, lugar);
            intent.putExtra(CrearSesionActivity.EXTRA_GRUPO_ID, grupoId);
            intent.putExtra(CrearSesionActivity.EXTRA_DESCRIPCION, getIntent().getStringExtra(EXTRA_DESCRIPCION));
            startActivity(intent);
        });
    }

    private void cargarAsistencia(long id) {
        ApiClient.getAsistenciaService().getAsistencia(id)
                .enqueue(new Callback<List<AsistenciaAtleta>>() {
                    @Override
                    public void onResponse(Call<List<AsistenciaAtleta>> call,
                                           Response<List<AsistenciaAtleta>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            procesarAsistencia(response.body());
                        }
                        if (esEntrenador) {
                            ((android.widget.ProgressBar) findViewById(R.id.progressAsistencia))
                                    .setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onFailure(Call<List<AsistenciaAtleta>> call, Throwable t) {
                        if (esEntrenador) {
                            ((android.widget.ProgressBar) findViewById(R.id.progressAsistencia))
                                    .setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void procesarAsistencia(List<AsistenciaAtleta> lista) {
        if (esEntrenador) {
            int presentes = 0, ausentes = 0, justificados = 0;
            for (AsistenciaAtleta a : lista) {
                if ("PRESENTE".equals(a.getEstado()))       presentes++;
                else if ("AUSENTE".equals(a.getEstado()))   ausentes++;
                else if ("JUSTIFICADO".equals(a.getEstado())) justificados++;
            }
            ((TextView) findViewById(R.id.tvCountPresentes)).setText(String.valueOf(presentes));
            ((TextView) findViewById(R.id.tvCountAusentes)).setText(String.valueOf(ausentes));
            ((TextView) findViewById(R.id.tvCountJustificados)).setText(String.valueOf(justificados));
        } else {
            // Buscar el estado personal del atleta autenticado
            // El backend debería retornar solo su registro; tomamos el primero
            if (!lista.isEmpty()) {
                AsistenciaAtleta mia = lista.get(0);
                String estado = mia.getEstado();
                if (estado != null) {
                    View layoutMia = findViewById(R.id.layoutMiAsistencia);
                    TextView tvMia = findViewById(R.id.tvMiAsistencia);
                    layoutMia.setVisibility(View.VISIBLE);
                    tvMia.setText(estadoLabel(estado));
                    tvMia.setBackgroundTintList(ColorStateList.valueOf(estadoColor(estado)));
                }
            }
        }
    }

    private String estadoLabel(String estado) {
        switch (estado) {
            case "PROGRAMADA":   return "Programada";
            case "ACTIVA":       return "Activa";
            case "FINALIZADA":   return "Finalizada";
            case "CANCELADA":    return "Cancelada";
            case "PRESENTE":     return "Presente";
            case "AUSENTE":      return "Ausente";
            case "JUSTIFICADO":  return "Justificado";
            default:             return estado;
        }
    }

    private int estadoColor(String estado) {
        switch (estado) {
            case "ACTIVA":
            case "PRESENTE":     return getColor(R.color.colorActive);
            case "CANCELADA":
            case "AUSENTE":      return getColor(R.color.colorCancelled);
            case "JUSTIFICADO":
            case "PROGRAMADA":   return getColor(R.color.colorPending);
            case "FINALIZADA":   return getColor(R.color.colorTextSecondary);
            default:             return getColor(R.color.colorPrimary);
        }
    }

    private String formatFechaHora(String inicio, String fin) {
        if (inicio == null) return "—";
        try {
            SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date d = iso.parse(inicio);
            if (d == null) return inicio;
            String fecha = new SimpleDateFormat("EEEE, dd 'de' MMMM", new Locale("es")).format(d);
            String horaI = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(d);
            String horaF = "—";
            if (fin != null) {
                Date df = iso.parse(fin);
                if (df != null) horaF = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(df);
            }
            return fecha.substring(0, 1).toUpperCase() + fecha.substring(1) + "  ·  " + horaI + " – " + horaF;
        } catch (ParseException e) {
            return inicio;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
