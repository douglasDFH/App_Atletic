package com.example.tallerappmovil.atletas;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.asistencia.HistorialAsistenciaActivity;
import com.example.tallerappmovil.marcas.MarcasAdapter;
import com.example.tallerappmovil.model.AtletaDetalle;
import com.example.tallerappmovil.model.MarcaPersonal;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AtletaPerfilActivity extends AppCompatActivity {

    private TextView tvAvatarGrande, tvNombreCompleto, tvDisciplinaPerfil;
    private TextView tvCategoriaPerfil, tvGrupoPerfil;
    private TextView tvTotalMarcas, tvTotalPRs, tvSinMarcas;
    private ProgressBar progressBar;
    private MarcasAdapter marcasAdapter;

    private Long atletaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atleta_perfil);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        atletaId = getIntent().getLongExtra(AtletasActivity.EXTRA_ATLETA_ID, -1L);
        String nombreExtra = getIntent().getStringExtra(AtletasActivity.EXTRA_ATLETA_NOMBRE);

        tvAvatarGrande    = findViewById(R.id.tvAvatarGrande);
        tvNombreCompleto  = findViewById(R.id.tvNombreCompleto);
        tvDisciplinaPerfil = findViewById(R.id.tvDisciplinaPerfil);
        tvCategoriaPerfil = findViewById(R.id.tvCategoriaPerfil);
        tvGrupoPerfil     = findViewById(R.id.tvGrupoPerfil);
        tvTotalMarcas     = findViewById(R.id.tvTotalMarcas);
        tvTotalPRs        = findViewById(R.id.tvTotalPRs);
        tvSinMarcas       = findViewById(R.id.tvSinMarcas);
        progressBar       = findViewById(R.id.progressBar);

        // Muestra el nombre mientras carga el detalle completo
        if (nombreExtra != null) {
            tvNombreCompleto.setText(nombreExtra);
            tvAvatarGrande.setText(String.valueOf(nombreExtra.charAt(0)).toUpperCase());
            if (getSupportActionBar() != null) getSupportActionBar().setTitle(nombreExtra);
        }

        RecyclerView recycler = findViewById(R.id.recyclerMarcas);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        marcasAdapter = new MarcasAdapter();
        marcasAdapter.setMostrarAtleta(false);
        recycler.setAdapter(marcasAdapter);

        // Ver historial de asistencia de este atleta
        View cardVerAsistencia = findViewById(R.id.cardVerAsistencia);
        if (atletaId != -1L) {
            cardVerAsistencia.setOnClickListener(v -> {
                Intent intent = new Intent(this, HistorialAsistenciaActivity.class);
                intent.putExtra(HistorialAsistenciaActivity.EXTRA_ATLETA_ID, atletaId);
                intent.putExtra(HistorialAsistenciaActivity.EXTRA_ATLETA_NOMBRE,
                        tvNombreCompleto.getText().toString());
                startActivity(intent);
            });
            cargarDetalle();
            cargarMarcas();
        }
    }

    private void cargarDetalle() {
        ApiClient.getAtletasService().getAtleta(atletaId)
                .enqueue(new Callback<AtletaDetalle>() {
                    @Override
                    public void onResponse(Call<AtletaDetalle> call, Response<AtletaDetalle> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            AtletaDetalle d = response.body();
                            String nombre = d.getNombreCompleto() != null ? d.getNombreCompleto() : "";
                            tvNombreCompleto.setText(nombre);
                            if (!nombre.isEmpty()) {
                                tvAvatarGrande.setText(String.valueOf(nombre.charAt(0)).toUpperCase());
                                if (getSupportActionBar() != null)
                                    getSupportActionBar().setTitle(nombre);
                            }
                            if (d.getDisciplina() != null && !d.getDisciplina().isEmpty()) {
                                tvDisciplinaPerfil.setText(d.getDisciplina());
                            }
                            if (d.getCategoria() != null && !d.getCategoria().isEmpty()) {
                                tvCategoriaPerfil.setText(d.getCategoria());
                                tvCategoriaPerfil.setVisibility(View.VISIBLE);
                            }
                            if (d.getGrupoNombre() != null && !d.getGrupoNombre().isEmpty()) {
                                String sep = tvCategoriaPerfil.getVisibility() == View.VISIBLE ? " · " : "";
                                tvGrupoPerfil.setText(sep + d.getGrupoNombre());
                                tvGrupoPerfil.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    @Override public void onFailure(Call<AtletaDetalle> call, Throwable t) {}
                });
    }

    private void cargarMarcas() {
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getMarcasService().getMarcasPorAtleta(atletaId, null)
                .enqueue(new Callback<List<MarcaPersonal>>() {
                    @Override
                    public void onResponse(Call<List<MarcaPersonal>> call,
                                           Response<List<MarcaPersonal>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            List<MarcaPersonal> lista = response.body();
                            marcasAdapter.setMarcas(lista);

                            int total = lista.size();
                            int prs   = 0;
                            for (MarcaPersonal m : lista) if (m.isEsMejorMarca()) prs++;

                            tvTotalMarcas.setText(String.valueOf(total));
                            tvTotalPRs.setText(String.valueOf(prs));
                            tvSinMarcas.setVisibility(total == 0 ? View.VISIBLE : View.GONE);
                        } else {
                            tvSinMarcas.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(Call<List<MarcaPersonal>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvSinMarcas.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
