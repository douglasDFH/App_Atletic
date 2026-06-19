package com.example.tallerappmovil.grupos;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.model.AtletaInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeleccionAtletaAdapter extends RecyclerView.Adapter<SeleccionAtletaAdapter.ViewHolder> {

    public interface OnToggleListener {
        void onToggle(AtletaInfo atleta, boolean estaEnGrupo);
    }

    private final List<AtletaInfo> todosList = new ArrayList<>();
    private final List<AtletaInfo> listaFiltrada = new ArrayList<>();
    private final Set<Long> enGrupo  = new HashSet<>();
    private final Set<Long> cargando = new HashSet<>();
    private OnToggleListener listener;

    public void setOnToggleListener(OnToggleListener l) { this.listener = l; }

    public void setAtletas(List<AtletaInfo> atletas, Set<Long> idsEnGrupo) {
        todosList.clear();
        todosList.addAll(atletas);
        listaFiltrada.clear();
        listaFiltrada.addAll(atletas);
        enGrupo.clear();
        enGrupo.addAll(idsEnGrupo);
        notifyDataSetChanged();
    }

    public void filtrar(String query) {
        listaFiltrada.clear();
        if (query == null || query.isEmpty()) {
            listaFiltrada.addAll(todosList);
        } else {
            String q = query.toLowerCase();
            for (AtletaInfo a : todosList) {
                if (a.getNombreCompleto() != null &&
                        a.getNombreCompleto().toLowerCase().contains(q)) {
                    listaFiltrada.add(a);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setEnGrupo(Long atletaId, boolean valor) {
        if (valor) enGrupo.add(atletaId);
        else       enGrupo.remove(atletaId);
        cargando.remove(atletaId);
        notifyItemChanged(posicion(atletaId));
    }

    public void setCargando(Long atletaId, boolean valor) {
        if (valor) cargando.add(atletaId);
        else       cargando.remove(atletaId);
        notifyItemChanged(posicion(atletaId));
    }

    public int countEnGrupo() { return enGrupo.size(); }

    private int posicion(Long id) {
        for (int i = 0; i < listaFiltrada.size(); i++) {
            if (id.equals(listaFiltrada.get(i).getId())) return i;
        }
        return -1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_atleta_seleccionable, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        AtletaInfo atleta = listaFiltrada.get(position);
        Long id = atleta.getId();
        String nombre = atleta.getNombreCompleto() != null ? atleta.getNombreCompleto() : "";

        h.tvNombre.setText(nombre);
        h.tvInicial.setText(nombre.isEmpty() ? "?" : String.valueOf(nombre.charAt(0)).toUpperCase());

        boolean seleccionado = enGrupo.contains(id);
        boolean loading      = cargando.contains(id);

        // Check circle color
        int bgColor = seleccionado
                ? h.itemView.getContext().getColor(R.color.colorPrimary)
                : h.itemView.getContext().getColor(R.color.colorBorder);
        h.checkCircle.setBackgroundTintList(ColorStateList.valueOf(bgColor));
        h.tvCheck.setVisibility(seleccionado ? View.VISIBLE : View.INVISIBLE);

        // Loading state
        h.progressItem.setVisibility(loading ? View.VISIBLE : View.GONE);
        h.checkCircle.setVisibility(loading ? View.GONE : View.VISIBLE);
        h.itemView.setEnabled(!loading);

        h.itemView.setOnClickListener(v -> {
            if (listener != null && !cargando.contains(atleta.getId())) {
                listener.onToggle(atleta, enGrupo.contains(atleta.getId()));
            }
        });
    }

    @Override
    public int getItemCount() { return listaFiltrada.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInicial, tvNombre, tvCheck;
        View checkCircle;
        ProgressBar progressItem;

        ViewHolder(View v) {
            super(v);
            tvInicial    = v.findViewById(R.id.tvInicial);
            tvNombre     = v.findViewById(R.id.tvNombreAtleta);
            tvCheck      = v.findViewById(R.id.tvCheck);
            checkCircle  = v.findViewById(R.id.checkCircle);
            progressItem = v.findViewById(R.id.progressItem);
        }
    }
}
