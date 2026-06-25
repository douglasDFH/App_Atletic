package com.example.tallerappmovil.disciplinas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.model.Disciplina;

import java.util.ArrayList;
import java.util.List;

public class DisciplinasAdapter extends RecyclerView.Adapter<DisciplinasAdapter.ViewHolder> {

    private List<Disciplina> disciplinas = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Disciplina disciplina);
    }

    public void setDisciplinas(List<Disciplina> lista) {
        this.disciplinas = lista;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener l) { this.listener = l; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_disciplina, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Disciplina d = disciplinas.get(position);

        h.tvUnidad.setText(d.getUnidad() != null ? d.getUnidad().toUpperCase() : "?");
        h.tvNombre.setText(d.getNombre());

        if (d.getDescripcion() != null && !d.getDescripcion().isEmpty()) {
            h.tvDescripcion.setText(d.getDescripcion());
            h.tvDescripcion.setVisibility(View.VISIBLE);
        } else {
            h.tvDescripcion.setVisibility(View.GONE);
        }

        String requisitos = d.descripcionRequisitos();
        if (!requisitos.equals("Sin requisitos físicos mínimos")) {
            h.tvRequisitos.setText(requisitos);
            h.tvRequisitos.setVisibility(View.VISIBLE);
        } else {
            h.tvRequisitos.setVisibility(View.GONE);
        }

        h.tvInactiva.setVisibility(d.isActiva() ? View.GONE : View.VISIBLE);

        h.itemView.setOnClickListener(v -> { if (listener != null) listener.onItemClick(d); });
    }

    @Override
    public int getItemCount() { return disciplinas.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUnidad, tvNombre, tvDescripcion, tvRequisitos, tvInactiva;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUnidad     = itemView.findViewById(R.id.tvUnidad);
            tvNombre     = itemView.findViewById(R.id.tvNombreDisciplina);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionDisciplina);
            tvRequisitos = itemView.findViewById(R.id.tvRequisitos);
            tvInactiva   = itemView.findViewById(R.id.tvInactiva);
        }
    }
}
