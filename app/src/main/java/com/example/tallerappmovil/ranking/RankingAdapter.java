package com.example.tallerappmovil.ranking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.model.MarcaPersonal;

import java.util.ArrayList;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private List<MarcaPersonal> entradas = new ArrayList<>();

    public void setEntradas(List<MarcaPersonal> lista) {
        this.entradas = lista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ranking, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        MarcaPersonal m = entradas.get(position);
        int rank = position + 1;

        switch (rank) {
            case 1: h.tvPosicion.setText("🥇"); break;
            case 2: h.tvPosicion.setText("🥈"); break;
            case 3: h.tvPosicion.setText("🥉"); break;
            default: h.tvPosicion.setText(String.valueOf(rank)); break;
        }

        String nombre = m.getAtletaNombre() != null ? m.getAtletaNombre() : "—";
        h.tvNombre.setText(nombre);
        h.tvDisciplina.setText(m.getDisciplina() != null ? m.getDisciplina() : "");

        String resultado = m.getResultado() != null ? m.getResultado() : "—";
        h.tvResultado.setText(resultado);
        h.tvUnidad.setText(m.getUnidad() != null ? m.getUnidad() : "");
    }

    @Override
    public int getItemCount() { return entradas.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPosicion, tvNombre, tvDisciplina, tvResultado, tvUnidad;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPosicion  = itemView.findViewById(R.id.tvPosicion);
            tvNombre    = itemView.findViewById(R.id.tvNombreRanking);
            tvDisciplina = itemView.findViewById(R.id.tvDisciplinaRanking);
            tvResultado = itemView.findViewById(R.id.tvResultadoRanking);
            tvUnidad    = itemView.findViewById(R.id.tvUnidadRanking);
        }
    }
}
