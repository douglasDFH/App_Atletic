package com.example.tallerappmovil.agenda;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.GrupoEntrenamiento;
import com.example.tallerappmovil.model.SesionCreateRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearSesionActivity extends AppCompatActivity {

    public static final String EXTRA_SESION_ID    = "sesionId";
    public static final String EXTRA_HORA_INICIO  = "horaInicio";
    public static final String EXTRA_HORA_FIN     = "horaFin";
    public static final String EXTRA_LUGAR        = "lugar";
    public static final String EXTRA_GRUPO_ID     = "grupoId";
    public static final String EXTRA_DESCRIPCION  = "descripcion";

    private TextInputLayout tilFecha, tilHoraInicio, tilHoraFin, tilLugar, tilGrupo;
    private TextInputEditText etFecha, etHoraInicio, etHoraFin, etLugar, etDescripcion;
    private AutoCompleteTextView spinnerGrupo;
    private MaterialButton btnGuardar, btnCancelarSesion;
    private ProgressBar progressBar;

    private int anio, mes, dia, horaIni, minIni, horaFinV, minFinV;
    private Long sesionId = null;
    private Long grupoIdSeleccionado = null;
    private List<GrupoEntrenamiento> grupos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_sesion);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tilFecha       = findViewById(R.id.tilFecha);
        tilHoraInicio  = findViewById(R.id.tilHoraInicio);
        tilHoraFin     = findViewById(R.id.tilHoraFin);
        tilLugar       = findViewById(R.id.tilLugar);
        tilGrupo       = findViewById(R.id.tilGrupo);
        etFecha        = findViewById(R.id.etFecha);
        etHoraInicio   = findViewById(R.id.etHoraInicio);
        etHoraFin      = findViewById(R.id.etHoraFin);
        etLugar        = findViewById(R.id.etLugar);
        etDescripcion  = findViewById(R.id.etDescripcion);
        spinnerGrupo   = findViewById(R.id.spinnerGrupo);
        btnGuardar     = findViewById(R.id.btnGuardar);
        btnCancelarSesion = findViewById(R.id.btnCancelarSesion);
        progressBar    = findViewById(R.id.progressBar);

        // Recuperar extras si viene en modo edición
        sesionId = getIntent().getLongExtra(EXTRA_SESION_ID, -1);
        if (sesionId == -1) sesionId = null;

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(sesionId == null
                    ? getString(R.string.lbl_crear_sesion)
                    : getString(R.string.lbl_editar_sesion));
        }

        if (sesionId != null) {
            btnCancelarSesion.setVisibility(View.VISIBLE);
            // Pre-llenar campos
            rellenarCampos(
                    getIntent().getStringExtra(EXTRA_HORA_INICIO),
                    getIntent().getStringExtra(EXTRA_HORA_FIN),
                    getIntent().getStringExtra(EXTRA_LUGAR),
                    getIntent().getLongExtra(EXTRA_GRUPO_ID, -1),
                    getIntent().getStringExtra(EXTRA_DESCRIPCION));
        } else {
            // Fecha por defecto: hoy
            Calendar hoy = Calendar.getInstance();
            anio = hoy.get(Calendar.YEAR);
            mes  = hoy.get(Calendar.MONTH);
            dia  = hoy.get(Calendar.DAY_OF_MONTH);
            actualizarCampoFecha();
        }

        etFecha.setOnClickListener(v -> mostrarDatePicker());
        etHoraInicio.setOnClickListener(v -> mostrarTimePicker(true));
        etHoraFin.setOnClickListener(v -> mostrarTimePicker(false));

        btnGuardar.setOnClickListener(v -> guardar());
        btnCancelarSesion.setOnClickListener(v -> confirmarCancelarSesion());

        cargarGrupos();
    }

    private void rellenarCampos(String horaInicio, String horaFin,
                                String lugar, long grupoId, String descripcion) {
        if (horaInicio != null && horaInicio.length() >= 16) {
            String[] parts = horaInicio.split("T");
            String[] fecha = parts[0].split("-");
            String[] hora  = parts[1].substring(0, 5).split(":");
            anio = Integer.parseInt(fecha[0]);
            mes  = Integer.parseInt(fecha[1]) - 1;
            dia  = Integer.parseInt(fecha[2]);
            horaIni = Integer.parseInt(hora[0]);
            minIni  = Integer.parseInt(hora[1]);
            actualizarCampoFecha();
            etHoraInicio.setText(String.format(Locale.getDefault(), "%02d:%02d", horaIni, minIni));
        }
        if (horaFin != null && horaFin.length() >= 16) {
            String[] hora = horaFin.split("T")[1].substring(0, 5).split(":");
            horaFinV = Integer.parseInt(hora[0]);
            minFinV  = Integer.parseInt(hora[1]);
            etHoraFin.setText(String.format(Locale.getDefault(), "%02d:%02d", horaFinV, minFinV));
        }
        if (lugar != null) etLugar.setText(lugar);
        if (descripcion != null) etDescripcion.setText(descripcion);
        grupoIdSeleccionado = grupoId > 0 ? grupoId : null;
    }

    private void cargarGrupos() {
        ApiClient.getAgendaService().listarGrupos().enqueue(new Callback<List<GrupoEntrenamiento>>() {
            @Override
            public void onResponse(Call<List<GrupoEntrenamiento>> call,
                                   Response<List<GrupoEntrenamiento>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    grupos = response.body();
                    List<String> nombres = new ArrayList<>();
                    for (GrupoEntrenamiento g : grupos) nombres.add(g.getNombre());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            CrearSesionActivity.this,
                            android.R.layout.simple_dropdown_item_1line, nombres);
                    spinnerGrupo.setAdapter(adapter);
                    // Pre-seleccionar si viene de edición
                    if (grupoIdSeleccionado != null) {
                        for (GrupoEntrenamiento g : grupos) {
                            if (g.getId().equals(grupoIdSeleccionado)) {
                                spinnerGrupo.setText(g.getNombre(), false);
                                break;
                            }
                        }
                    } else if (!grupos.isEmpty()) {
                        spinnerGrupo.setText(grupos.get(0).getNombre(), false);
                        grupoIdSeleccionado = grupos.get(0).getId();
                    }
                    spinnerGrupo.setOnItemClickListener((parent, view, position, id) ->
                            grupoIdSeleccionado = grupos.get(position).getId());
                } else {
                    String msg = extractErrorMessage(response);
                    Toast.makeText(CrearSesionActivity.this,
                            msg != null ? msg : "Error " + response.code() + " al cargar grupos",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<GrupoEntrenamiento>> call, Throwable t) {
                Toast.makeText(CrearSesionActivity.this,
                        getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDatePicker() {
        new DatePickerDialog(this, (view, y, m, d) -> {
            anio = y; mes = m; dia = d;
            actualizarCampoFecha();
        }, anio, mes, dia).show();
    }

    private void mostrarTimePicker(boolean esInicio) {
        int hActual = esInicio ? horaIni : horaFinV;
        int mActual = esInicio ? minIni  : minFinV;
        new TimePickerDialog(this, (view, hora, min) -> {
            if (esInicio) { horaIni = hora; minIni = min;
                etHoraInicio.setText(String.format(Locale.getDefault(), "%02d:%02d", hora, min));
            } else { horaFinV = hora; minFinV = min;
                etHoraFin.setText(String.format(Locale.getDefault(), "%02d:%02d", hora, min));
            }
        }, hActual, mActual, true).show();
    }

    private void actualizarCampoFecha() {
        etFecha.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", dia, mes + 1, anio));
    }

    private void guardar() {
        tilFecha.setError(null); tilHoraInicio.setError(null);
        tilHoraFin.setError(null); tilLugar.setError(null); tilGrupo.setError(null);

        String lugar = etLugar.getText() != null ? etLugar.getText().toString().trim() : "";
        String horaInicioStr = etHoraInicio.getText() != null
                ? etHoraInicio.getText().toString() : "";
        String horaFinStr = etHoraFin.getText() != null
                ? etHoraFin.getText().toString() : "";

        if (horaInicioStr.isEmpty()) { tilHoraInicio.setError(getString(R.string.err_campo_requerido)); return; }
        if (horaFinStr.isEmpty())    { tilHoraFin.setError(getString(R.string.err_campo_requerido)); return; }
        if (lugar.isEmpty())         { tilLugar.setError(getString(R.string.err_campo_requerido)); return; }
        if (grupoIdSeleccionado == null) { tilGrupo.setError(getString(R.string.err_campo_requerido)); return; }

        // Validar que fin > inicio
        int inicioMin = horaIni * 60 + minIni;
        int finMin    = horaFinV * 60 + minFinV;
        if (finMin <= inicioMin) { tilHoraFin.setError(getString(R.string.err_hora_invalida)); return; }

        // Construir ISO datetime
        String fechaBase = String.format(Locale.getDefault(), "%04d-%02d-%02d", anio, mes + 1, dia);
        String isoInicio = fechaBase + "T" + String.format(Locale.getDefault(), "%02d:%02d:00", horaIni, minIni);
        String isoFin    = fechaBase + "T" + String.format(Locale.getDefault(), "%02d:%02d:00", horaFinV, minFinV);
        String descripcion = etDescripcion.getText() != null
                ? etDescripcion.getText().toString().trim() : "";

        SesionCreateRequest req = new SesionCreateRequest(
                grupoIdSeleccionado, isoInicio, isoFin, lugar, descripcion);

        setLoading(true);

        Callback<Void> callback = new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(CrearSesionActivity.this,
                            sesionId == null
                                    ? getString(R.string.msg_sesion_creada)
                                    : getString(R.string.msg_sesion_actualizada),
                            Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String msg = extractErrorMessage(response);
                    Toast.makeText(CrearSesionActivity.this,
                            msg != null ? msg : "Error " + response.code() + " al guardar sesión",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                setLoading(false);
                Toast.makeText(CrearSesionActivity.this,
                        getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
            }
        };

        if (sesionId == null) {
            ApiClient.getAgendaService().crear(req).enqueue(callback);
        } else {
            ApiClient.getAgendaService().editar(sesionId, req).enqueue(callback);
        }
    }

    private void confirmarCancelarSesion() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.btn_cancelar_sesion))
                .setMessage("Ingresa el motivo de la cancelación:")
                .setView(createMotivoInput())
                .setPositiveButton("Confirmar", (d, w) -> cancelarSesion(motivoTemporal))
                .setNegativeButton("Volver", null)
                .show();
    }

    private String motivoTemporal = "";
    private android.widget.EditText etMotivo;

    private android.widget.EditText createMotivoInput() {
        etMotivo = new android.widget.EditText(this);
        etMotivo.setHint(getString(R.string.hint_motivo));
        etMotivo.setPadding(48, 24, 48, 24);
        etMotivo.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void onTextChanged(CharSequence s, int st, int b, int c) { motivoTemporal = s.toString(); }
            public void afterTextChanged(android.text.Editable s) {}
        });
        return etMotivo;
    }

    private void cancelarSesion(String motivo) {
        if (sesionId == null) return;
        setLoading(true);
        Map<String, String> body = new HashMap<>();
        body.put("motivo", motivo);
        ApiClient.getAgendaService().cancelar(sesionId, body)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        setLoading(false);
                        Toast.makeText(CrearSesionActivity.this,
                                getString(R.string.msg_sesion_cancelada), Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(CrearSesionActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String extractErrorMessage(retrofit2.Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String body = response.errorBody().string();
                int idx = body.indexOf("\"message\":");
                if (idx >= 0) {
                    int start = body.indexOf('"', idx + 10) + 1;
                    int end   = body.indexOf('"', start);
                    if (start > 0 && end > start) return body.substring(start, end);
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnGuardar.setEnabled(!loading);
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
