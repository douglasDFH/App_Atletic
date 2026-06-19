package com.example.tallerappmovil.grupos;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.GrupoEntrenamiento;
import com.example.tallerappmovil.model.GrupoRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearGrupoActivity extends AppCompatActivity {

    private TextInputLayout tilNombre;
    private TextInputEditText etNombre, etDescripcion;
    private AutoCompleteTextView spinnerDisciplina;
    private MaterialButton btnGuardar;
    private ProgressBar progressBar;

    private static final String[] DISCIPLINAS = {
            "100m", "200m", "400m", "Salto Largo", "Lanzamiento de Bala", "Gimnasia", "Múltiples"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_grupo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tilNombre        = findViewById(R.id.tilNombre);
        etNombre         = findViewById(R.id.etNombre);
        etDescripcion    = findViewById(R.id.etDescripcion);
        spinnerDisciplina = findViewById(R.id.spinnerDisciplina);
        btnGuardar       = findViewById(R.id.btnGuardar);
        progressBar      = findViewById(R.id.progressBar);

        ArrayAdapter<String> discAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, DISCIPLINAS);
        spinnerDisciplina.setAdapter(discAdapter);
        spinnerDisciplina.setText(DISCIPLINAS[0], false);

        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void guardar() {
        tilNombre.setError(null);

        String nombre = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
        if (nombre.isEmpty()) {
            tilNombre.setError(getString(R.string.err_campo_requerido));
            return;
        }

        String disciplina   = spinnerDisciplina.getText().toString().trim();
        String descripcion  = etDescripcion.getText() != null
                ? etDescripcion.getText().toString().trim() : "";

        GrupoRequest req = new GrupoRequest(nombre, disciplina, descripcion);

        setLoading(true);
        ApiClient.getGruposService().crearGrupo(req)
                .enqueue(new Callback<GrupoEntrenamiento>() {
                    @Override
                    public void onResponse(Call<GrupoEntrenamiento> call,
                                           Response<GrupoEntrenamiento> response) {
                        setLoading(false);
                        if (response.isSuccessful()) {
                            Toast.makeText(CrearGrupoActivity.this,
                                    getString(R.string.msg_grupo_creado), Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(CrearGrupoActivity.this,
                                    getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<GrupoEntrenamiento> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(CrearGrupoActivity.this,
                                getString(R.string.err_conexion), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnGuardar.setEnabled(!loading);
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
