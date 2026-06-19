package com.example.tallerappmovil.notificaciones;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.Notificacion;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificacionesActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private TextView tvVacio, tvCountNoLeidas, tvMarcarTodas;
    private View progressBar, layoutBanner;
    private NotificacionesAdapter adapter;
    private final List<Notificacion> lista = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar     = findViewById(R.id.progressBar);
        tvVacio         = findViewById(R.id.tvVacio);
        recycler        = findViewById(R.id.recyclerNotificaciones);
        layoutBanner    = findViewById(R.id.layoutBannerNoLeidas);
        tvCountNoLeidas = findViewById(R.id.tvCountNoLeidas);
        tvMarcarTodas   = findViewById(R.id.tvMarcarTodas);

        adapter = new NotificacionesAdapter(lista);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        adapter.setOnItemClickListener(n -> {
            if (!n.isLeida()) marcarLeida(n);
        });

        tvMarcarTodas.setOnClickListener(v -> marcarTodasLeidas());

        cargarNotificaciones();
    }

    private void cargarNotificaciones() {
        progressBar.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);
        recycler.setVisibility(View.GONE);

        ApiClient.getNotificacionesService().getNotificaciones()
                .enqueue(new Callback<List<Notificacion>>() {
                    @Override
                    public void onResponse(Call<List<Notificacion>> call, Response<List<Notificacion>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            lista.clear();
                            lista.addAll(response.body());
                            adapter.notifyDataSetChanged();
                            actualizarBanner();
                            recycler.setVisibility(lista.isEmpty() ? View.GONE : View.VISIBLE);
                            tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
                        } else {
                            tvVacio.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Notificacion>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        tvVacio.setVisibility(View.VISIBLE);
                        Toast.makeText(NotificacionesActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void marcarLeida(Notificacion n) {
        ApiClient.getNotificacionesService().marcarLeida(n.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        n.setLeida(true);
                        int pos = lista.indexOf(n);
                        if (pos >= 0) adapter.notifyItemChanged(pos);
                        actualizarBanner();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) { }
                });
    }

    private void marcarTodasLeidas() {
        ApiClient.getNotificacionesService().marcarTodasLeidas()
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        for (Notificacion n : lista) n.setLeida(true);
                        adapter.notifyDataSetChanged();
                        actualizarBanner();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) { }
                });
    }

    private void actualizarBanner() {
        long noLeidas = 0;
        for (Notificacion n : lista) if (!n.isLeida()) noLeidas++;
        if (noLeidas > 0) {
            layoutBanner.setVisibility(View.VISIBLE);
            tvCountNoLeidas.setText(getString(R.string.lbl_notif_no_leidas, (int) noLeidas));
        } else {
            layoutBanner.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
