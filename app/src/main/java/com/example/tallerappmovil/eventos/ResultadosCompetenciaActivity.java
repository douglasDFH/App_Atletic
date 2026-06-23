package com.example.tallerappmovil.eventos;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.AtletaInfo;
import com.example.tallerappmovil.model.ResultadoCompetencia;
import com.example.tallerappmovil.model.ResultadoRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultadosCompetenciaActivity extends AppCompatActivity {

    public static final String EXTRA_COMP_ID      = "compId";
    public static final String EXTRA_COMP_NOMBRE  = "compNombre";
    public static final String EXTRA_PUEDE_EDITAR = "puedeEditar";

    private long compId;
    private boolean puedeEditar;
    private LinearLayout container;
    private TextView tvVacio;
    private ProgressBar progressBar;
    private final List<AtletaInfo> inscritos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados_competencia);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        compId      = getIntent().getLongExtra(EXTRA_COMP_ID, -1L);
        puedeEditar = getIntent().getBooleanExtra(EXTRA_PUEDE_EDITAR, false);
        String nombre = getIntent().getStringExtra(EXTRA_COMP_NOMBRE);
        if (nombre != null && getSupportActionBar() != null) getSupportActionBar().setSubtitle(nombre);

        container   = findViewById(R.id.containerResultados);
        tvVacio     = findViewById(R.id.tvVacio);
        progressBar = findViewById(R.id.progressBar);

        FloatingActionButton fab = findViewById(R.id.fabAgregarResultado);
        if (puedeEditar) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v -> abrirDialogoRegistrar());
            cargarInscritos();
        }

        cargarResultados();
    }

    private void cargarInscritos() {
        ApiClient.getCompetenciasService().getInscritos(compId)
                .enqueue(new Callback<List<AtletaInfo>>() {
                    @Override public void onResponse(Call<List<AtletaInfo>> call, Response<List<AtletaInfo>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            inscritos.clear();
                            inscritos.addAll(response.body());
                        }
                    }
                    @Override public void onFailure(Call<List<AtletaInfo>> call, Throwable t) {}
                });
    }

    private void cargarResultados() {
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getCompetenciasService().getResultados(compId)
                .enqueue(new Callback<List<ResultadoCompetencia>>() {
                    @Override public void onResponse(Call<List<ResultadoCompetencia>> call,
                                                     Response<List<ResultadoCompetencia>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            mostrar(response.body());
                        } else {
                            tvVacio.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override public void onFailure(Call<List<ResultadoCompetencia>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvVacio.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void mostrar(List<ResultadoCompetencia> lista) {
        container.removeAllViews();
        tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        for (ResultadoCompetencia r : lista) {
            container.addView(crearFila(r));
        }
    }

    private View crearFila(ResultadoCompetencia r) {
        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.VERTICAL);
        fila.setPadding(dp(16), dp(12), dp(16), dp(12));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(10);
        fila.setLayoutParams(lp);
        fila.setBackgroundResource(R.drawable.bg_card_stat);

        String pos = r.getPosicion() != null ? r.getPosicion() + "º  " : "";
        String pr = r.isEsMarcaPersonal() ? "  ⭐ Récord" : "";
        TextView t1 = new TextView(this);
        t1.setText(pos + (r.getAtletaNombre() != null ? r.getAtletaNombre() : "Atleta") + pr);
        t1.setTextColor(getColor(R.color.colorTextPrimary));
        t1.setTextSize(15f);
        fila.addView(t1);

        TextView t2 = new TextView(this);
        String marca = r.getMarca() != null ? r.getMarca() : "—";
        String obs = r.getObservaciones() != null && !r.getObservaciones().isEmpty()
                ? "  ·  " + r.getObservaciones() : "";
        t2.setText("Marca: " + marca + obs);
        t2.setTextColor(getColor(R.color.colorTextSecondary));
        t2.setTextSize(13f);
        fila.addView(t2);
        return fila;
    }

    private void abrirDialogoRegistrar() {
        if (inscritos.isEmpty()) {
            Toast.makeText(this, "No hay atletas inscritos en esta competencia", Toast.LENGTH_LONG).show();
            return;
        }
        View view = getLayoutInflater().inflate(R.layout.dialog_registrar_resultado, null);
        AutoCompleteTextView spinner = view.findViewById(R.id.spinnerAtleta);
        TextInputEditText etPosicion = view.findViewById(R.id.etPosicion);
        TextInputEditText etMarca = view.findViewById(R.id.etMarca);
        TextInputEditText etObs = view.findViewById(R.id.etObservaciones);

        List<String> nombres = new ArrayList<>();
        for (AtletaInfo a : inscritos) nombres.add(a.getNombreCompleto());
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nombres));
        final int[] sel = {0};
        spinner.setText(nombres.get(0), false);
        spinner.setOnItemClickListener((p, v, pos, id) -> sel[0] = pos);

        new AlertDialog.Builder(this)
                .setTitle(R.string.lbl_registrar_resultado)
                .setView(view)
                .setPositiveButton(R.string.btn_registrar, (d, w) -> {
                    AtletaInfo atleta = inscritos.get(sel[0]);
                    Integer posicion = null;
                    String posStr = etPosicion.getText() != null ? etPosicion.getText().toString().trim() : "";
                    if (!posStr.isEmpty()) {
                        try { posicion = Integer.parseInt(posStr); } catch (NumberFormatException ignored) {}
                    }
                    String marca = etMarca.getText() != null ? etMarca.getText().toString().trim() : "";
                    String obs = etObs.getText() != null ? etObs.getText().toString().trim() : "";
                    registrar(new ResultadoRequest(atleta.getId(), posicion,
                            marca.isEmpty() ? null : marca, obs.isEmpty() ? null : obs));
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void registrar(ResultadoRequest req) {
        ApiClient.getCompetenciasService().registrarResultado(compId, req)
                .enqueue(new Callback<ResultadoCompetencia>() {
                    @Override public void onResponse(Call<ResultadoCompetencia> call, Response<ResultadoCompetencia> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ResultadosCompetenciaActivity.this,
                                    getString(R.string.msg_resultado_guardado), Toast.LENGTH_SHORT).show();
                            cargarResultados();
                        } else {
                            Toast.makeText(ResultadosCompetenciaActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onFailure(Call<ResultadoCompetencia> call, Throwable t) {
                        Toast.makeText(ResultadosCompetenciaActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
