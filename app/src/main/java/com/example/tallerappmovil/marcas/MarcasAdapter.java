package com.example.tallerappmovil.marcas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.model.MarcaPersonal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MarcasAdapter extends RecyclerView.Adapter<MarcasAdapter.ViewHolder> {

    public interface OnEliminarListener {
        void onEliminar(MarcaPersonal marca);
    }

    private List<MarcaPersonal> marcas = new ArrayList<>();
    private boolean mostrarAtleta = false;
    private boolean mostrarEliminar = false;
    private OnEliminarListener eliminarListener;

    private static final SimpleDateFormat ISO = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat DIA = new SimpleDateFormat("dd", Locale.getDefault());
    private static final SimpleDateFormat MES = new SimpleDateFormat("MMM", new Locale("es"));

    public void setMarcas(List<MarcaPersonal> lista) {
        this.marcas = lista;
        notifyDataSetChanged();
    }

    public List<MarcaPersonal> getMarcas() { return marcas; }

    public void setMostrarAtleta(boolean mostrar) { this.mostrarAtleta = mostrar; }

    public void setMostrarEliminar(boolean mostrar, OnEliminarListener listener) {
        this.mostrarEliminar = mostrar;
        this.eliminarListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_marca, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        MarcaPersonal m = marcas.get(position);

        try {
            Date d = ISO.parse(m.getFecha());
            if (d != null) {
                h.tvDia.setText(DIA.format(d));
                h.tvMes.setText(MES.format(d).toUpperCase(Locale.getDefault()));
            }
        } catch (Exception e) {
            h.tvDia.setText("--");
            h.tvMes.setText("---");
        }

        h.tvDisciplina.setText(m.getDisciplina());

        String resultado = m.getResultado() != null ? m.getResultado() : "--";
        String unidad    = m.getUnidad() != null ? " " + m.getUnidad() : "";
        h.tvResultado.setText(resultado + unidad);

        h.tvPR.setVisibility(m.isEsMejorMarca() ? View.VISIBLE : View.GONE);

        if (mostrarAtleta && m.getAtletaNombre() != null) {
            h.tvAtletaNombre.setText("👤 " + m.getAtletaNombre());
            h.tvAtletaNombre.setVisibility(View.VISIBLE);
        } else {
            h.tvAtletaNombre.setVisibility(View.GONE);
        }

        if (m.getObservaciones() != null && !m.getObservaciones().isEmpty()) {
            h.tvObservaciones.setText(m.getObservaciones());
            h.tvObservaciones.setVisibility(View.VISIBLE);
        } else {
            h.tvObservaciones.setVisibility(View.GONE);
        }

        if (mostrarEliminar && eliminarListener != null) {
            h.tvEliminar.setVisibility(View.VISIBLE);
            h.tvEliminar.setOnClickListener(v -> {
                int pos = h.getAdapterPosition();
                if (pos != RecyclerView.NO_ID) eliminarListener.onEliminar(marcas.get(pos));
            });
        } else {
            h.tvEliminar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() { return marcas.size(); }

    public void eliminarItem(Long id) {
        for (int i = 0; i < marcas.size(); i++) {
            if (id.equals(marcas.get(i).getId())) {
                marcas.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDia, tvMes, tvDisciplina, tvResultado, tvPR, tvAtletaNombre, tvObservaciones, tvEliminar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDia           = itemView.findViewById(R.id.tvDia);
            tvMes           = itemView.findViewById(R.id.tvMes);
            tvDisciplina    = itemView.findViewById(R.id.tvDisciplina);
            tvResultado     = itemView.findViewById(R.id.tvResultado);
            tvPR            = itemView.findViewById(R.id.tvPR);
            tvAtletaNombre  = itemView.findViewById(R.id.tvAtletaNombre);
            tvObservaciones = itemView.findViewById(R.id.tvObservaciones);
            tvEliminar      = itemView.findViewById(R.id.tvEliminar);
        }
    }
}
