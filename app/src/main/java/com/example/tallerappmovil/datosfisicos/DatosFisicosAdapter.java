package com.example.tallerappmovil.datosfisicos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.model.DatosFisicos;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatosFisicosAdapter extends RecyclerView.Adapter<DatosFisicosAdapter.ViewHolder> {

    private List<DatosFisicos> lista = new ArrayList<>();

    public void setLista(List<DatosFisicos> datos) {
        this.lista = datos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dato_fisico, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        DatosFisicos d = lista.get(position);

        h.tvImc.setText(d.getImc() != null
                ? String.format(Locale.getDefault(), "%.1f", d.getImc()) : "—");

        h.tvFecha.setText(formatFecha(d.getFecha()));
        h.tvPeso.setText(d.getPesoKg() != null
                ? String.format(Locale.getDefault(), "%.1f kg", d.getPesoKg()) : "—");
        h.tvAltura.setText(d.getAlturaCm() != null
                ? String.format(Locale.getDefault(), "%.0f cm", d.getAlturaCm()) : "—");

        if (d.getMasaMuscularKg() != null) {
            h.tvMasa.setText(String.format(Locale.getDefault(), "Musc: %.1f kg", d.getMasaMuscularKg()));
            h.tvMasa.setVisibility(View.VISIBLE);
        } else {
            h.tvMasa.setVisibility(View.GONE);
        }

        if (d.getPorcentajeGrasa() != null) {
            h.tvGrasa.setText(String.format(Locale.getDefault(), "Grasa: %.1f%%", d.getPorcentajeGrasa()));
            h.tvGrasa.setVisibility(View.VISIBLE);
        } else {
            h.tvGrasa.setVisibility(View.GONE);
        }

        if (d.getObservaciones() != null && !d.getObservaciones().isEmpty()) {
            h.tvObservaciones.setText(d.getObservaciones());
            h.tvObservaciones.setVisibility(View.VISIBLE);
        } else {
            h.tvObservaciones.setVisibility(View.GONE);
        }

        String registrador = d.getRegistradoPorNombre() != null ? d.getRegistradoPorNombre() : "—";
        h.tvRegistradoPor.setText("Registrado por: " + registrador);
    }

    @Override
    public int getItemCount() { return lista.size(); }

    private String formatFecha(String fecha) {
        if (fecha == null || fecha.length() < 10) return fecha != null ? fecha : "—";
        try {
            String[] p = fecha.substring(0, 10).split("-");
            return p[2] + "/" + p[1] + "/" + p[0];
        } catch (Exception e) { return fecha; }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvImc, tvFecha, tvPeso, tvAltura, tvMasa, tvGrasa, tvObservaciones, tvRegistradoPor;

        ViewHolder(@NonNull View v) {
            super(v);
            tvImc          = v.findViewById(R.id.tvImc);
            tvFecha        = v.findViewById(R.id.tvFechaDato);
            tvPeso         = v.findViewById(R.id.tvPeso);
            tvAltura       = v.findViewById(R.id.tvAltura);
            tvMasa         = v.findViewById(R.id.tvMasa);
            tvGrasa        = v.findViewById(R.id.tvGrasa);
            tvObservaciones = v.findViewById(R.id.tvObservaciones);
            tvRegistradoPor = v.findViewById(R.id.tvRegistradoPor);
        }
    }
}
