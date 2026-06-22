package com.example.tallerappmovil.atletas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
import com.example.tallerappmovil.model.PadreInfo;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AtletaPerfilActivity extends AppCompatActivity {

    private TextView tvAvatarGrande, tvNombreCompleto, tvDisciplinaPerfil;
    private TextView tvCategoriaPerfil, tvGrupoPerfil;
    private TextView tvTotalMarcas, tvTotalPRs, tvSinMarcas;
    private TextView tvTutorInfo, tvPadreVinculado;
    private MaterialButton btnVincularPadre;
    private ProgressBar progressBar;
    private MarcasAdapter marcasAdapter;

    private Long atletaId;
    private Long padreVinculadoId; // cuenta de padre actualmente vinculada a este atleta

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
        tvTutorInfo       = findViewById(R.id.tvTutorInfo);
        tvPadreVinculado  = findViewById(R.id.tvPadreVinculado);
        btnVincularPadre  = findViewById(R.id.btnVincularPadre);
        progressBar       = findViewById(R.id.progressBar);

        btnVincularPadre.setOnClickListener(v -> abrirDialogoVincular());

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
                            mostrarTutor(d);
                        }
                    }
                    @Override public void onFailure(Call<AtletaDetalle> call, Throwable t) {}
                });
    }

    private void mostrarTutor(AtletaDetalle d) {
        // Contacto de emergencia del tutor
        if (d.getTutorNombre() != null && !d.getTutorNombre().isEmpty()) {
            String parent = d.getTutorParentesco() != null ? " (" + d.getTutorParentesco() + ")" : "";
            String tel = d.getTutorTelefono() != null ? "  ·  📞 " + d.getTutorTelefono() : "";
            String menor = d.isEsMenor() ? "  ·  MENOR" : "";
            tvTutorInfo.setText(d.getTutorNombre() + parent + tel + menor);
        } else {
            String edad = d.getEdad() != null ? d.getEdad() + " años" : "Sin fecha de nacimiento";
            tvTutorInfo.setText(edad + (d.isEsMenor() ? "  ·  MENOR (sin datos de tutor)" : ""));
        }

        // Cuenta de padre/tutor vinculada
        padreVinculadoId = d.getTutorVinculadoId();
        if (d.getTutorVinculadoNombre() != null && !d.getTutorVinculadoNombre().isEmpty()) {
            tvPadreVinculado.setText("Cuenta vinculada: " + d.getTutorVinculadoNombre());
        } else {
            tvPadreVinculado.setText(getString(R.string.lbl_sin_vinculo));
        }
    }

    private void abrirDialogoVincular() {
        ApiClient.getUsuariosService().getPadres().enqueue(new Callback<List<PadreInfo>>() {
            @Override
            public void onResponse(Call<List<PadreInfo>> call, Response<List<PadreInfo>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().isEmpty()) {
                    Toast.makeText(AtletaPerfilActivity.this,
                            "No hay cuentas de padre/tutor registradas", Toast.LENGTH_LONG).show();
                    return;
                }
                mostrarSelectorPadres(response.body());
            }
            @Override
            public void onFailure(Call<List<PadreInfo>> call, Throwable t) {
                Toast.makeText(AtletaPerfilActivity.this,
                        getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarSelectorPadres(List<PadreInfo> padres) {
        List<PadreInfo> opciones = new ArrayList<>(padres);
        List<String> labels = new ArrayList<>();
        for (PadreInfo p : opciones) {
            String yaTiene = p.getHijoNombre() != null ? "  (vinculado a " + p.getHijoNombre() + ")" : "";
            labels.add(p.getNombreCompleto() + yaTiene);
        }

        AlertDialog.Builder b = new AlertDialog.Builder(this)
                .setTitle(R.string.lbl_elegir_padre)
                .setItems(labels.toArray(new String[0]), (dialog, which) ->
                        vincular(opciones.get(which).getId()));

        // Si ya hay una cuenta vinculada, permitir desvincular
        if (padreVinculadoId != null) {
            b.setNeutralButton(R.string.lbl_desvincular, (dialog, which) -> desvincular(padreVinculadoId));
        }
        b.setNegativeButton(android.R.string.cancel, null);
        b.show();
    }

    private void vincular(Long padreId) {
        ApiClient.getUsuariosService().vincularHijo(padreId, atletaId).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AtletaPerfilActivity.this,
                            getString(R.string.msg_vinculo_guardado), Toast.LENGTH_SHORT).show();
                    cargarDetalle();
                } else {
                    Toast.makeText(AtletaPerfilActivity.this,
                            getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AtletaPerfilActivity.this,
                        getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void desvincular(Long padreId) {
        ApiClient.getUsuariosService().desvincularHijo(padreId).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {
                cargarDetalle();
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {}
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
