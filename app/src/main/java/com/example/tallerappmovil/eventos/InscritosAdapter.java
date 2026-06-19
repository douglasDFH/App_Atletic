package com.example.tallerappmovil.eventos;

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

public class InscritosAdapter extends RecyclerView.Adapter<InscritosAdapter.ViewHolder> {

    private List<AtletaInfo> inscritos = new ArrayList<>();

    public void setInscritos(List<AtletaInfo> lista) {
        this.inscritos = lista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inscrito, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        AtletaInfo a = inscritos.get(position);
        String nombre = a.getNombreCompleto() != null ? a.getNombreCompleto() : "?";
        h.tvInicial.setText(String.valueOf(nombre.charAt(0)).toUpperCase(Locale.getDefault()));
        h.tvNombre.setText(nombre);
        if (a.getDisciplina() != null && !a.getDisciplina().isEmpty()) {
            h.tvDisciplina.setText(a.getDisciplina());
            h.tvDisciplina.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() { return inscritos.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInicial, tvNombre, tvDisciplina;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInicial   = itemView.findViewById(R.id.tvInicialInscrito);
            tvNombre    = itemView.findViewById(R.id.tvNombreInscrito);
            tvDisciplina = itemView.findViewById(R.id.tvDisciplinaInscrito);
        }
    }
}
