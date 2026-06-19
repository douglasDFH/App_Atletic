package com.example.tallerappmovil.grupos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.model.GrupoEntrenamiento;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GruposAdapter extends RecyclerView.Adapter<GruposAdapter.ViewHolder> {

    private List<GrupoEntrenamiento> grupos = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(GrupoEntrenamiento grupo);
    }

    public void setGrupos(List<GrupoEntrenamiento> lista) {
        this.grupos = lista;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener l) { this.listener = l; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grupo, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        GrupoEntrenamiento g = grupos.get(position);

        String nombre = g.getNombre() != null ? g.getNombre() : "?";
        h.tvInicial.setText(String.valueOf(nombre.charAt(0)).toUpperCase(Locale.getDefault()));
        h.tvNombre.setText(nombre);

        if (g.getDisciplina() != null && !g.getDisciplina().isEmpty()) {
            h.tvDisciplina.setText(g.getDisciplina());
        }

        h.itemView.setOnClickListener(v -> { if (listener != null) listener.onItemClick(g); });
    }

    @Override
    public int getItemCount() { return grupos.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInicial, tvNombre, tvDisciplina, tvAtletas;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInicial   = itemView.findViewById(R.id.tvInicialGrupo);
            tvNombre    = itemView.findViewById(R.id.tvNombreGrupo);
            tvDisciplina = itemView.findViewById(R.id.tvDisciplinaGrupo);
            tvAtletas   = itemView.findViewById(R.id.tvAtletasGrupo);
        }
    }
}
