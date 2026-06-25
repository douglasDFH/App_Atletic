package com.example.tallerappmovil.datosfisicos;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.model.DatosFisicos;
import com.example.tallerappmovil.model.DatosFisicosRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrarDatosFisicosActivity extends AppCompatActivity {

    private TextInputEditText etFecha, etPeso, etAltura, etMasa, etGrasa, etObservaciones;
    private TextView tvImcPreview, tvImcCategoria;
    private MaterialButton btnGuardar;

    private Long atletaId;
    private int anio, mes, dia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_datos_fisicos);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        atletaId = getIntent().getLongExtra(DatosFisicosActivity.EXTRA_ATLETA_ID, -1L);
        String atletaNombre = getIntent().getStringExtra(DatosFisicosActivity.EXTRA_ATLETA_NOMBRE);
        if (atletaNombre != null && getSupportActionBar() != null)
            getSupportActionBar().setTitle("Medición: " + atletaNombre);

        etFecha         = findViewById(R.id.etFecha);
        etPeso          = findViewById(R.id.etPeso);
        etAltura        = findViewById(R.id.etAltura);
        etMasa          = findViewById(R.id.etMasa);
        etGrasa         = findViewById(R.id.etGrasa);
        etObservaciones = findViewById(R.id.etObservaciones);
        tvImcPreview    = findViewById(R.id.tvImcPreview);
        tvImcCategoria  = findViewById(R.id.tvImcCategoria);
        btnGuardar      = findViewById(R.id.btnGuardar);

        Calendar hoy = Calendar.getInstance();
        anio = hoy.get(Calendar.YEAR);
        mes  = hoy.get(Calendar.MONTH);
        dia  = hoy.get(Calendar.DAY_OF_MONTH);
        actualizarFecha();

        etFecha.setOnClickListener(v -> mostrarDatePicker());

        TextWatcher imcWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override public void afterTextChanged(Editable s) { calcularImc(); }
        };
        etPeso.addTextChangedListener(imcWatcher);
        etAltura.addTextChangedListener(imcWatcher);

        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void calcularImc() {
        try {
            double peso   = Double.parseDouble(etPeso.getText().toString().trim());
            double altura = Double.parseDouble(etAltura.getText().toString().trim());
            if (peso <= 0 || altura <= 0) { limpiarImc(); return; }
            double alturaM = altura / 100.0;
            double imc = peso / (alturaM * alturaM);
            tvImcPreview.setText(String.format(Locale.getDefault(), "%.1f", imc));
            tvImcCategoria.setText(categoriaImc(imc));
            tvImcCategoria.setVisibility(View.VISIBLE);
        } catch (NumberFormatException e) {
            limpiarImc();
        }
    }

    private void limpiarImc() {
        tvImcPreview.setText("—");
        tvImcCategoria.setVisibility(View.GONE);
    }

    private String categoriaImc(double imc) {
        if (imc < 18.5) return "Bajo peso";
        if (imc < 25.0) return "Peso normal";
        if (imc < 30.0) return "Sobrepeso";
        return "Obesidad";
    }

    private void mostrarDatePicker() {
        new DatePickerDialog(this, (view, y, m, d) -> {
            anio = y; mes = m; dia = d;
            actualizarFecha();
        }, anio, mes, dia).show();
    }

    private void actualizarFecha() {
        etFecha.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", dia, mes + 1, anio));
    }

    private void guardar() {
        String pesoStr   = text(etPeso);
        String alturaStr = text(etAltura);

        if (pesoStr.isEmpty()) { etPeso.setError("Requerido"); return; }
        if (alturaStr.isEmpty()) { etAltura.setError("Requerido"); return; }

        double peso, altura;
        try {
            peso   = Double.parseDouble(pesoStr);
            altura = Double.parseDouble(alturaStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valores numéricos inválidos", Toast.LENGTH_SHORT).show();
            return;
        }

        String fechaIso = String.format(Locale.getDefault(), "%04d-%02d-%02d", anio, mes + 1, dia);
        Double masa  = parseDoubleOrNull(text(etMasa));
        Double grasa = parseDoubleOrNull(text(etGrasa));
        String obs   = text(etObservaciones);

        DatosFisicosRequest req = new DatosFisicosRequest(
                atletaId, fechaIso, peso, altura,
                masa, grasa, obs.isEmpty() ? null : obs);

        btnGuardar.setEnabled(false);
        ApiClient.getDatosFisicosService().registrar(req).enqueue(new Callback<DatosFisicos>() {
            @Override
            public void onResponse(Call<DatosFisicos> c, Response<DatosFisicos> r) {
                btnGuardar.setEnabled(true);
                if (r.isSuccessful()) {
                    Toast.makeText(RegistrarDatosFisicosActivity.this,
                            "Medición registrada", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegistrarDatosFisicosActivity.this,
                            "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DatosFisicos> c, Throwable t) {
                btnGuardar.setEnabled(true);
                Toast.makeText(RegistrarDatosFisicosActivity.this,
                        "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String text(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private Double parseDoubleOrNull(String s) {
        if (s.isEmpty()) return null;
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { return null; }
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
