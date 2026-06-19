package com.example.tallerappmovil.atletas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.model.AtletaInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AtletasAdapter extends RecyclerView.Adapter<AtletasAdapter.ViewHolder> {

    private List<AtletaInfo> todosList = new ArrayList<>();
    private List<AtletaInfo> listaFiltrada = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AtletaInfo atleta);
    }

    public void setAtletas(List<AtletaInfo> lista) {
        this.todosList = lista;
        this.listaFiltrada = new ArrayList<>(lista);
        notifyDataSetChanged();
    }

    public void filtrar(String query) {
        listaFiltrada.clear();
        if (query == null || query.trim().isEmpty()) {
            listaFiltrada.addAll(todosList);
        } else {
            String q = query.toLowerCase(Locale.getDefault());
            for (AtletaInfo a : todosList) {
                if (a.getNombreCompleto() != null &&
                        a.getNombreCompleto().toLowerCase(Locale.getDefault()).contains(q)) {
                    listaFiltrada.add(a);
                }
            }
        }
        notifyDataSetChanged();
    }

    public int getTotalFiltrado() { return listaFiltrada.size(); }

    public void setOnItemClickListener(OnItemClickListener l) { this.listener = l; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_atleta_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        AtletaInfo a = listaFiltrada.get(position);

        String nombre = a.getNombreCompleto() != null ? a.getNombreCompleto() : "?";
        h.tvInicial.setText(String.valueOf(nombre.charAt(0)).toUpperCase());
        h.tvNombre.setText(nombre);

        if (a.getDisciplina() != null && !a.getDisciplina().isEmpty()) {
            h.tvDisciplina.setText(a.getDisciplina());
        } else {
            h.tvDisciplina.setVisibility(View.GONE);
        }

        h.itemView.setOnClickListener(v -> { if (listener != null) listener.onItemClick(a); });
    }

    @Override
    public int getItemCount() { return listaFiltrada.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInicial, tvNombre, tvDisciplina;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInicial   = itemView.findViewById(R.id.tvInicial);
            tvNombre    = itemView.findViewById(R.id.tvNombreAtleta);
            tvDisciplina = itemView.findViewById(R.id.tvDisciplinaAtleta);
        }
    }
}
