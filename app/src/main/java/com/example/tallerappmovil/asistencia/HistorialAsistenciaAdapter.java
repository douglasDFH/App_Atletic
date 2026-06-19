package com.example.tallerappmovil.asistencia;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.model.AsistenciaHistorial;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistorialAsistenciaAdapter
        extends RecyclerView.Adapter<HistorialAsistenciaAdapter.ViewHolder> {

    private List<AsistenciaHistorial> todosItems = new ArrayList<>();
    private List<AsistenciaHistorial> filteredItems = new ArrayList<>();
    private String filtroEstado = null; // null = todos

    private static final SimpleDateFormat ISO =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat DIA =
            new SimpleDateFormat("dd", Locale.getDefault());
    private static final SimpleDateFormat MES =
            new SimpleDateFormat("MMM", new Locale("es"));
    private static final SimpleDateFormat HORA =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    public void setItems(List<AsistenciaHistorial> lista) {
        this.todosItems = lista;
        aplicarFiltro(filtroEstado);
    }

    public void aplicarFiltro(String estado) {
        this.filtroEstado = estado;
        filteredItems.clear();
        if (estado == null) {
            filteredItems.addAll(todosItems);
        } else {
            for (AsistenciaHistorial h : todosItems) {
                if (estado.equals(h.getEstado())) filteredItems.add(h);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial_asistencia, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        AsistenciaHistorial item = filteredItems.get(position);

        try {
            Date d = ISO.parse(item.getHoraInicio());
            if (d != null) {
                h.tvDia.setText(DIA.format(d));
                h.tvMes.setText(MES.format(d).toUpperCase(Locale.getDefault()));
                String hora = HORA.format(d);
                String lugar = item.getLugar() != null ? " · " + item.getLugar() : "";
                h.tvHoraLugar.setText(hora + lugar);
            }
        } catch (Exception e) {
            h.tvDia.setText("--");
            h.tvMes.setText("---");
        }

        h.tvGrupo.setText(item.getGrupoNombre() != null ? item.getGrupoNombre() : "Entrenamiento");

        String estado = item.getEstado() != null ? item.getEstado() : "";
        h.tvEstado.setText(estadoLabel(estado));
        h.tvEstado.setBackgroundTintList(
                ColorStateList.valueOf(estadoColor(estado, h.itemView)));
    }

    @Override
    public int getItemCount() { return filteredItems.size(); }

    private String estadoLabel(String e) {
        switch (e) {
            case "PRESENTE":    return "PRESENTE";
            case "AUSENTE":     return "AUSENTE";
            case "JUSTIFICADO": return "JUSTIF.";
            default:            return e;
        }
    }

    private int estadoColor(String e, View ctx) {
        switch (e) {
            case "PRESENTE":    return ctx.getContext().getColor(R.color.colorActive);
            case "AUSENTE":     return ctx.getContext().getColor(R.color.colorCancelled);
            case "JUSTIFICADO": return ctx.getContext().getColor(R.color.colorPending);
            default:            return ctx.getContext().getColor(R.color.colorSurfaceVariant);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDia, tvMes, tvGrupo, tvHoraLugar, tvEstado;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDia      = itemView.findViewById(R.id.tvDia);
            tvMes      = itemView.findViewById(R.id.tvMes);
            tvGrupo    = itemView.findViewById(R.id.tvGrupoHistorial);
            tvHoraLugar = itemView.findViewById(R.id.tvHoraLugar);
            tvEstado   = itemView.findViewById(R.id.tvEstadoBadge);
        }
    }
}
