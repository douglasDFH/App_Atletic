package com.example.tallerappmovil.asistencia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.model.ReporteAtleta;

import java.util.List;

public class ReporteAsistenciaAdapter extends RecyclerView.Adapter<ReporteAsistenciaAdapter.ViewHolder> {

    private final List<ReporteAtleta> lista;

    public ReporteAsistenciaAdapter(List<ReporteAtleta> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reporte_atleta, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        ReporteAtleta r = lista.get(position);

        h.tvNombre.setText(r.getNombreCompleto());

        int pct = Math.round(r.getPorcentajeAsistencia());
        // Si el backend no calcula el porcentaje, lo calculamos localmente
        if (pct == 0 && r.getTotalSesiones() > 0) {
            pct = Math.round(r.getPresentes() * 100f / r.getTotalSesiones());
        }
        h.tvPorcentaje.setText(pct + "%");
        h.progressBar.setProgress(pct);

        // Color dinámico del porcentaje según umbral
        int color;
        if (pct >= 80) {
            color = h.itemView.getContext().getColor(R.color.colorActive);
        } else if (pct >= 50) {
            color = h.itemView.getContext().getColor(R.color.colorPending);
        } else {
            color = h.itemView.getContext().getColor(R.color.colorCancelled);
        }
        h.tvPorcentaje.setTextColor(color);
        h.progressBar.setProgressTintList(
                android.content.res.ColorStateList.valueOf(color));

        h.tvPresentes.setText(String.valueOf(r.getPresentes()));
        h.tvAusentes.setText(String.valueOf(r.getAusentes()));
        h.tvJustificados.setText(String.valueOf(r.getJustificados()));
        h.tvTotal.setText(String.valueOf(r.getTotalSesiones()));
    }

    @Override
    public int getItemCount() { return lista.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPorcentaje, tvPresentes, tvAusentes, tvJustificados, tvTotal;
        ProgressBar progressBar;

        ViewHolder(View v) {
            super(v);
            tvNombre       = v.findViewById(R.id.tvNombreAtleta);
            tvPorcentaje   = v.findViewById(R.id.tvPorcentaje);
            progressBar    = v.findViewById(R.id.progressAsistencia);
            tvPresentes    = v.findViewById(R.id.tvPresentes);
            tvAusentes     = v.findViewById(R.id.tvAusentes);
            tvJustificados = v.findViewById(R.id.tvJustificados);
            tvTotal        = v.findViewById(R.id.tvTotalSesiones);
        }
    }
}
