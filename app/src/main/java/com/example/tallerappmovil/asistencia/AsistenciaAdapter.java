package com.example.tallerappmovil.asistencia;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.model.AsistenciaAtleta;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsistenciaAdapter extends RecyclerView.Adapter<AsistenciaAdapter.ViewHolder> {

    public interface OnCambioListener { void onCambio(); }

    private List<AsistenciaAtleta> atletas = new ArrayList<>();
    private final Map<Long, String> estados = new HashMap<>();
    private OnCambioListener listener;

    public void setAtletas(List<AsistenciaAtleta> lista) {
        this.atletas = lista;
        estados.clear();
        for (AsistenciaAtleta a : lista) {
            if (a.getEstado() != null) estados.put(a.getAtletaId(), a.getEstado());
        }
        notifyDataSetChanged();
    }

    public void setOnCambioListener(OnCambioListener l) { this.listener = l; }

    public Map<Long, String> getEstados() { return estados; }

    public boolean todosSeleccionados() {
        if (atletas.isEmpty()) return false;
        for (AsistenciaAtleta a : atletas) {
            if (!estados.containsKey(a.getAtletaId())) return false;
        }
        return true;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_atleta_asistencia, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        AsistenciaAtleta atleta = atletas.get(position);
        String nombre = atleta.getNombreCompleto();

        h.tvNombre.setText(nombre);
        h.tvInicial.setText(nombre.isEmpty() ? "?" :
                String.valueOf(nombre.charAt(0)).toUpperCase());

        String estado = estados.getOrDefault(atleta.getAtletaId(), "");
        actualizarBotones(h, estado, h.itemView.getContext());

        h.btnPresente.setOnClickListener(v -> cambiarEstado(h, atleta.getAtletaId(), "PRESENTE", v.getContext()));
        h.btnAusente.setOnClickListener(v -> cambiarEstado(h, atleta.getAtletaId(), "AUSENTE", v.getContext()));
        h.btnJustificado.setOnClickListener(v -> cambiarEstado(h, atleta.getAtletaId(), "JUSTIFICADO", v.getContext()));
    }

    private void cambiarEstado(ViewHolder h, Long atletaId, String nuevoEstado, android.content.Context ctx) {
        estados.put(atletaId, nuevoEstado);
        actualizarBotones(h, nuevoEstado, ctx);
        if (listener != null) listener.onCambio();
    }

    private void actualizarBotones(ViewHolder h, String estado, android.content.Context ctx) {
        int defaultColor  = ctx.getColor(R.color.colorSurfaceVariant);
        int presenteColor = ctx.getColor(R.color.colorPrimary);
        int ausenteColor  = ctx.getColor(R.color.colorCancelled);
        int justifColor   = ctx.getColor(R.color.colorPending);

        h.btnPresente.setBackgroundTintList(ColorStateList.valueOf(
                "PRESENTE".equals(estado) ? presenteColor : defaultColor));
        h.btnAusente.setBackgroundTintList(ColorStateList.valueOf(
                "AUSENTE".equals(estado) ? ausenteColor : defaultColor));
        h.btnJustificado.setBackgroundTintList(ColorStateList.valueOf(
                "JUSTIFICADO".equals(estado) ? justifColor : defaultColor));
    }

    @Override
    public int getItemCount() { return atletas.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInicial, tvNombre;
        MaterialButton btnPresente, btnAusente, btnJustificado;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInicial      = itemView.findViewById(R.id.tvInicial);
            tvNombre       = itemView.findViewById(R.id.tvNombreAtleta);
            btnPresente    = itemView.findViewById(R.id.btnPresente);
            btnAusente     = itemView.findViewById(R.id.btnAusente);
            btnJustificado = itemView.findViewById(R.id.btnJustificado);
        }
    }
}
