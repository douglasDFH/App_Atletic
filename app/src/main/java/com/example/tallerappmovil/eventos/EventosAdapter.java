package com.example.tallerappmovil.eventos;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.model.Competencia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventosAdapter extends RecyclerView.Adapter<EventosAdapter.ViewHolder> {

    private List<Competencia> competencias = new ArrayList<>();
    private OnItemClickListener listener;

    private static final SimpleDateFormat ISO = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat DIA  = new SimpleDateFormat("dd",   Locale.getDefault());
    private static final SimpleDateFormat MES  = new SimpleDateFormat("MMM",  new Locale("es"));
    private static final SimpleDateFormat ANIO = new SimpleDateFormat("yyyy", Locale.getDefault());

    public interface OnItemClickListener {
        void onItemClick(Competencia c);
    }

    public void setCompetencias(List<Competencia> lista) {
        this.competencias = lista;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener l) { this.listener = l; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_competencia, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Competencia c = competencias.get(position);

        try {
            Date d = ISO.parse(c.getFechaEvento());
            if (d != null) {
                h.tvDia.setText(DIA.format(d));
                h.tvMes.setText(MES.format(d).toUpperCase(Locale.getDefault()));
                h.tvAnio.setText(ANIO.format(d));
            }
        } catch (Exception e) {
            h.tvDia.setText("--");
            h.tvMes.setText("---");
            h.tvAnio.setText("----");
        }

        h.tvNombreEvento.setText(c.getNombre());
        h.tvDisciplina.setText(c.getDisciplina() != null ? c.getDisciplina() : "");

        String est = c.getEstado() != null ? c.getEstado() : "";
        h.tvEstado.setText(estadoLabel(est));
        h.tvEstado.setBackgroundTintList(ColorStateList.valueOf(estadoColor(est, h.itemView)));

        h.tvCategoria.setText(c.getCategoria() != null ? "Categoria: " + c.getCategoria() : "");
        h.tvLugar.setText(c.getLugar() != null ? c.getLugar() : "");

        h.itemView.setOnClickListener(v -> { if (listener != null) listener.onItemClick(c); });
    }

    @Override
    public int getItemCount() { return competencias.size(); }

    private String estadoLabel(String estado) {
        switch (estado) {
            case "PROXIMO":    return "PRÓXIMO";
            case "EN_CURSO":   return "EN CURSO";
            case "FINALIZADO": return "FINALIZADO";
            case "CANCELADO":  return "CANCELADO";
            default:           return estado;
        }
    }

    private int estadoColor(String estado, View ctx) {
        switch (estado) {
            case "PROXIMO":    return ctx.getContext().getColor(R.color.colorPrimary);
            case "EN_CURSO":   return ctx.getContext().getColor(R.color.colorActive);
            case "FINALIZADO": return ctx.getContext().getColor(R.color.colorTextSecondary);
            case "CANCELADO":  return ctx.getContext().getColor(R.color.colorCancelled);
            default:           return ctx.getContext().getColor(R.color.colorSurfaceVariant);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDia, tvMes, tvAnio, tvNombreEvento, tvDisciplina, tvEstado, tvCategoria, tvLugar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDia          = itemView.findViewById(R.id.tvDia);
            tvMes          = itemView.findViewById(R.id.tvMes);
            tvAnio         = itemView.findViewById(R.id.tvAnio);
            tvNombreEvento = itemView.findViewById(R.id.tvNombreEvento);
            tvDisciplina   = itemView.findViewById(R.id.tvDisciplina);
            tvEstado       = itemView.findViewById(R.id.tvEstado);
            tvCategoria    = itemView.findViewById(R.id.tvCategoria);
            tvLugar        = itemView.findViewById(R.id.tvLugar);
        }
    }
}
