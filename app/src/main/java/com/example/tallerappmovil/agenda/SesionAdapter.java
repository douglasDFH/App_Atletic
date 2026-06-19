package com.example.tallerappmovil.agenda;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.model.SesionEntrenamiento;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SesionAdapter extends RecyclerView.Adapter<SesionAdapter.ViewHolder> {

    public interface OnSesionClickListener {
        void onClick(SesionEntrenamiento sesion);
    }

    private List<SesionEntrenamiento> sesiones = new ArrayList<>();
    private final boolean puedeEditar;
    private final OnSesionClickListener listener;

    private static final SimpleDateFormat ISO_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat DIA_SEMANA =
            new SimpleDateFormat("EEE", new Locale("es"));
    private static final SimpleDateFormat DIA_NUM =
            new SimpleDateFormat("dd", Locale.getDefault());
    private static final SimpleDateFormat MES =
            new SimpleDateFormat("MMM", new Locale("es"));
    private static final SimpleDateFormat HORA =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    public SesionAdapter(boolean puedeEditar, OnSesionClickListener listener) {
        this.puedeEditar = puedeEditar;
        this.listener    = listener;
    }

    public void setSesiones(List<SesionEntrenamiento> lista) {
        this.sesiones = lista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sesion, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        SesionEntrenamiento s = sesiones.get(position);

        try {
            Date inicio = ISO_FORMAT.parse(s.getHoraInicio());
            Date fin    = ISO_FORMAT.parse(s.getHoraFin());
            if (inicio != null && fin != null) {
                h.tvDiaSemana.setText(DIA_SEMANA.format(inicio).toUpperCase(Locale.getDefault()));
                h.tvDiaNumero.setText(DIA_NUM.format(inicio));
                h.tvMes.setText(MES.format(inicio).toUpperCase(Locale.getDefault()));
                h.tvHorario.setText(HORA.format(inicio) + " - " + HORA.format(fin));
            }
        } catch (ParseException e) {
            h.tvHorario.setText(s.getHoraInicio());
        }

        h.tvLugar.setText("📍 " + s.getLugar());
        h.tvGrupo.setText("👥 " + (s.getGrupoNombre() != null ? s.getGrupoNombre() : ""));

        // Estado badge
        String estado = s.getEstado() != null ? s.getEstado() : "PROGRAMADA";
        h.tvEstado.setText(estado);
        switch (estado) {
            case "CANCELADA":
                h.tvEstado.setBackgroundColor(Color.parseColor("#EF4444"));
                h.tvMotivo.setVisibility(View.VISIBLE);
                h.tvMotivo.setText("Motivo: " + (s.getMotivoCancelacion() != null
                        ? s.getMotivoCancelacion() : "—"));
                h.itemView.setAlpha(0.65f);
                break;
            case "ACTIVA":
                h.tvEstado.setBackgroundColor(Color.parseColor("#10B981"));
                h.tvMotivo.setVisibility(View.GONE);
                h.itemView.setAlpha(1f);
                break;
            case "FINALIZADA":
                h.tvEstado.setBackgroundColor(Color.parseColor("#9E9E9E"));
                h.tvMotivo.setVisibility(View.GONE);
                h.itemView.setAlpha(0.8f);
                break;
            default: // PROGRAMADA
                h.tvEstado.setBackgroundColor(Color.parseColor("#00BFA5"));
                h.tvMotivo.setVisibility(View.GONE);
                h.itemView.setAlpha(1f);
                break;
        }

        if (puedeEditar && !"CANCELADA".equals(estado) && !"FINALIZADA".equals(estado)) {
            h.itemView.setOnClickListener(v -> listener.onClick(s));
        } else {
            h.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() { return sesiones.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDiaSemana, tvDiaNumero, tvMes, tvHorario, tvLugar, tvGrupo, tvEstado, tvMotivo;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDiaSemana = itemView.findViewById(R.id.tvDiaSemana);
            tvDiaNumero = itemView.findViewById(R.id.tvDiaNumero);
            tvMes       = itemView.findViewById(R.id.tvMes);
            tvHorario   = itemView.findViewById(R.id.tvHorario);
            tvLugar     = itemView.findViewById(R.id.tvLugar);
            tvGrupo     = itemView.findViewById(R.id.tvGrupo);
            tvEstado    = itemView.findViewById(R.id.tvEstado);
            tvMotivo    = itemView.findViewById(R.id.tvMotivo);
        }
    }
}
