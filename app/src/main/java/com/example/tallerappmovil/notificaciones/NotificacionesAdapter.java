package com.example.tallerappmovil.notificaciones;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.model.Notificacion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionesAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(Notificacion n);
    }

    private final List<Notificacion> lista;
    private OnItemClickListener listener;

    public NotificacionesAdapter(List<Notificacion> lista) {
        this.lista = lista;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.listener = l;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notificacion, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Notificacion n = lista.get(position);

        h.tvIcono.setText(iconoParaTipo(n.getTipo()));
        h.tvTitulo.setText(n.getTitulo());
        h.tvMensaje.setText(n.getMensaje());
        h.tvFecha.setText(formatearFecha(n.getFecha()));

        h.viewPunto.setVisibility(n.isLeida() ? View.GONE : View.VISIBLE);

        // Oscurecer ligeramente si ya fue leída
        float alpha = n.isLeida() ? 0.6f : 1f;
        h.tvTitulo.setAlpha(alpha);

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(n);
        });
    }

    @Override
    public int getItemCount() { return lista.size(); }

    private String iconoParaTipo(String tipo) {
        if (tipo == null) return "🔔";
        switch (tipo) {
            case "SESION_NUEVA":            return "📅";
            case "ASISTENCIA_REGISTRADA":   return "✅";
            case "COMPETENCIA_NUEVA":       return "🏆";
            case "NUEVA_MARCA":             return "⭐";
            case "RECORDATORIO":            return "⏰";
            default:                        return "🔔";
        }
    }

    private String formatearFecha(String iso) {
        if (iso == null || iso.isEmpty()) return "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date fecha = sdf.parse(iso);
            if (fecha == null) return iso;

            Calendar hoy = Calendar.getInstance();
            Calendar ayer = Calendar.getInstance();
            ayer.add(Calendar.DAY_OF_YEAR, -1);
            Calendar fechaCal = Calendar.getInstance();
            fechaCal.setTime(fecha);

            boolean esHoy = hoy.get(Calendar.YEAR) == fechaCal.get(Calendar.YEAR)
                    && hoy.get(Calendar.DAY_OF_YEAR) == fechaCal.get(Calendar.DAY_OF_YEAR);
            boolean esAyer = ayer.get(Calendar.YEAR) == fechaCal.get(Calendar.YEAR)
                    && ayer.get(Calendar.DAY_OF_YEAR) == fechaCal.get(Calendar.DAY_OF_YEAR);

            if (esHoy) {
                return "Hoy " + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(fecha);
            } else if (esAyer) {
                return "Ayer " + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(fecha);
            } else {
                return new SimpleDateFormat("dd/MM · HH:mm", Locale.getDefault()).format(fecha);
            }
        } catch (ParseException e) {
            return iso;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcono, tvTitulo, tvMensaje, tvFecha;
        View viewPunto;

        ViewHolder(View v) {
            super(v);
            tvIcono   = v.findViewById(R.id.tvIconoTipo);
            tvTitulo  = v.findViewById(R.id.tvTituloNotif);
            tvMensaje = v.findViewById(R.id.tvMensajeNotif);
            tvFecha   = v.findViewById(R.id.tvFechaNotif);
            viewPunto = v.findViewById(R.id.viewPuntoNoLeida);
        }
    }
}
