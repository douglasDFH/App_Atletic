package com.example.tallerappmovil.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.model.Notificacion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActividadRecenteAdapter extends RecyclerView.Adapter<ActividadRecenteAdapter.VH> {

    private List<Notificacion> lista = new ArrayList<>();
    private Runnable onItemClick;

    private static final SimpleDateFormat ISO =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat HORA =
            new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat FECHA_CORTA =
            new SimpleDateFormat("dd/MM", Locale.getDefault());

    public void setLista(List<Notificacion> data) {
        this.lista = data;
        notifyDataSetChanged();
    }

    public void setOnItemClick(Runnable r) { this.onItemClick = r; }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_actividad_reciente, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Notificacion n = lista.get(position);

        h.tvIcono.setText(iconoPorTipo(n.getTipo()));
        h.tvTitulo.setText(n.getTitulo() != null ? n.getTitulo() : "");
        h.tvMensaje.setText(n.getMensaje() != null ? n.getMensaje() : "");
        h.tvTiempo.setText(formatearTiempo(n.getFecha()));
        h.viewPunto.setVisibility(n.isLeida() ? View.GONE : View.VISIBLE);

        h.itemView.setOnClickListener(v -> { if (onItemClick != null) onItemClick.run(); });
    }

    @Override
    public int getItemCount() { return lista.size(); }

    private String iconoPorTipo(String tipo) {
        if (tipo == null) return "🔔";
        switch (tipo) {
            case "SESION_NUEVA":          return "📅";
            case "ASISTENCIA_REGISTRADA": return "✅";
            case "COMPETENCIA_NUEVA":     return "🏆";
            case "NUEVA_MARCA":           return "⭐";
            case "RECORDATORIO":          return "⏰";
            default:                      return "🔔";
        }
    }

    private String formatearTiempo(String fechaIso) {
        if (fechaIso == null) return "";
        try {
            Date fecha = ISO.parse(fechaIso);
            if (fecha == null) return "";

            Calendar hoy    = Calendar.getInstance();
            Calendar fCal   = Calendar.getInstance();
            fCal.setTime(fecha);

            boolean mismoAnio = hoy.get(Calendar.YEAR)        == fCal.get(Calendar.YEAR);
            boolean mismoDia  = mismoAnio
                    && hoy.get(Calendar.DAY_OF_YEAR) == fCal.get(Calendar.DAY_OF_YEAR);
            boolean ayer      = mismoAnio
                    && hoy.get(Calendar.DAY_OF_YEAR) - fCal.get(Calendar.DAY_OF_YEAR) == 1;

            if (mismoDia) return "Hoy " + HORA.format(fecha);
            if (ayer)     return "Ayer " + HORA.format(fecha);
            return FECHA_CORTA.format(fecha) + " " + HORA.format(fecha);
        } catch (Exception e) {
            return "";
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvIcono, tvTitulo, tvMensaje, tvTiempo;
        View viewPunto;

        VH(@NonNull View v) {
            super(v);
            tvIcono   = v.findViewById(R.id.tvIconoActividad);
            tvTitulo  = v.findViewById(R.id.tvTituloActividad);
            tvMensaje = v.findViewById(R.id.tvMensajeActividad);
            tvTiempo  = v.findViewById(R.id.tvTiempoActividad);
            viewPunto = v.findViewById(R.id.viewPuntoNoLeida);
        }
    }
}
