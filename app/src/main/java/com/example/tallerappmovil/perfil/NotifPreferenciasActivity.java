package com.example.tallerappmovil.perfil;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.NotifPreferenciasRequest;
import com.example.tallerappmovil.model.PerfilUsuario;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotifPreferenciasActivity extends AppCompatActivity {

    private SwitchMaterial switchSesiones, switchCompetencias, switchResultados;
    private MaterialButton btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_preferencias);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.lbl_notif_preferencias);
        }

        switchSesiones    = findViewById(R.id.switchSesiones);
        switchCompetencias = findViewById(R.id.switchCompetencias);
        switchResultados  = findViewById(R.id.switchResultados);
        btnGuardar        = findViewById(R.id.btnGuardarNotif);

        cargarPreferencias();

        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void cargarPreferencias() {
        ApiClient.getUsuariosService().getPerfil()
                .enqueue(new Callback<PerfilUsuario>() {
                    @Override
                    public void onResponse(Call<PerfilUsuario> call, Response<PerfilUsuario> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            PerfilUsuario p = response.body();
                            // null = no configurado → activo por defecto
                            switchSesiones.setChecked(p.getNotifSesiones() == null || p.getNotifSesiones());
                            switchCompetencias.setChecked(p.getNotifCompetencias() == null || p.getNotifCompetencias());
                            switchResultados.setChecked(p.getNotifResultados() == null || p.getNotifResultados());
                        }
                    }
                    @Override public void onFailure(Call<PerfilUsuario> call, Throwable t) {}
                });
    }

    private void guardar() {
        btnGuardar.setEnabled(false);

        NotifPreferenciasRequest req = new NotifPreferenciasRequest(
                switchSesiones.isChecked(),
                switchCompetencias.isChecked(),
                switchResultados.isChecked()
        );

        ApiClient.getUsuariosService().actualizarNotifPreferencias(req)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        btnGuardar.setEnabled(true);
                        if (response.isSuccessful()) {
                            Toast.makeText(NotifPreferenciasActivity.this,
                                    getString(R.string.msg_preferencias_guardadas),
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(NotifPreferenciasActivity.this,
                                    getString(R.string.err_conexion),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        btnGuardar.setEnabled(true);
                        Toast.makeText(NotifPreferenciasActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
