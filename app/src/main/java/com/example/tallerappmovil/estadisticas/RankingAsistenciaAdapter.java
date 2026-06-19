package com.example.tallerappmovil.estadisticas;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.model.ReporteAtleta;

import java.util.ArrayList;
import java.util.List;

public class RankingAsistenciaAdapter extends RecyclerView.Adapter<RankingAsistenciaAdapter.VH> {

    private List<ReporteAtleta> lista = new ArrayList<>();

    public void setLista(List<ReporteAtleta> data) {
        this.lista = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_atleta_ranking, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ReporteAtleta r = lista.get(position);
        h.tvPosicion.setText(String.valueOf(position + 1));

        String nombre = r.getNombreCompleto() != null ? r.getNombreCompleto() : "";
        h.tvNombre.setText(nombre);
        h.tvInicial.setText(nombre.isEmpty() ? "?" :
                String.valueOf(nombre.charAt(0)).toUpperCase());

        float pct = r.getPorcentajeAsistencia();
        if (pct == 0f && r.getTotalSesiones() > 0) {
            pct = (r.getPresentes() * 100f) / r.getTotalSesiones();
        }
        int pctInt = Math.round(pct);
        h.tvPorcentaje.setText(pctInt + "%");
        h.progressAsistencia.setProgress(pctInt);

        int color;
        if (pctInt >= 80) color = h.itemView.getContext().getColor(R.color.colorActive);
        else if (pctInt >= 50) color = h.itemView.getContext().getColor(R.color.colorPending);
        else color = h.itemView.getContext().getColor(R.color.colorCancelled);

        h.progressAsistencia.setProgressTintList(ColorStateList.valueOf(color));
        h.tvPorcentaje.setTextColor(color);

        // Medalla para top 3
        if (position == 0) h.tvPosicion.setText("🥇");
        else if (position == 1) h.tvPosicion.setText("🥈");
        else if (position == 2) h.tvPosicion.setText("🥉");
    }

    @Override
    public int getItemCount() { return lista.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvPosicion, tvInicial, tvNombre, tvPorcentaje;
        ProgressBar progressAsistencia;

        VH(@NonNull View v) {
            super(v);
            tvPosicion        = v.findViewById(R.id.tvPosicion);
            tvInicial         = v.findViewById(R.id.tvInicial);
            tvNombre          = v.findViewById(R.id.tvNombre);
            tvPorcentaje      = v.findViewById(R.id.tvPorcentaje);
            progressAsistencia = v.findViewById(R.id.progressAsistencia);
        }
    }
}
