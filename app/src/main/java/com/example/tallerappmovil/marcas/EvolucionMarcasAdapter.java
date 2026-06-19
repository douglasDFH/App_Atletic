package com.example.tallerappmovil.marcas;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.model.MarcaPersonal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EvolucionMarcasAdapter extends RecyclerView.Adapter<EvolucionMarcasAdapter.ViewHolder> {

    private final List<MarcaPersonal> lista;
    private float valorMaximo = 1f;

    public EvolucionMarcasAdapter(List<MarcaPersonal> lista) {
        this.lista = lista;
    }

    public void setValorMaximo(float max) {
        this.valorMaximo = max == 0 ? 1f : max;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_marca_evolucion, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        MarcaPersonal m = lista.get(position);

        // Fecha
        parseFecha(m.getFecha(), h.tvDia, h.tvMes);

        // Resultado
        h.tvResultado.setText(m.getResultado() != null ? m.getResultado() : "—");
        h.tvUnidad.setText(m.getUnidad() != null ? m.getUnidad() : "");

        // PR badge
        h.tvPRBadge.setVisibility(m.isEsMejorMarca() ? View.VISIBLE : View.GONE);

        // Barra relativa: proporción respecto al valor máximo
        try {
            float valor = Float.parseFloat(m.getResultado().replace(",", "."));
            int progreso = Math.round((valor / valorMaximo) * 100);
            h.barraRelativa.setProgress(Math.min(100, progreso));
        } catch (Exception e) {
            h.barraRelativa.setProgress(0);
        }

        // Color del punto y barra: teal si PR, gris si no
        int color = m.isEsMejorMarca()
                ? h.itemView.getContext().getColor(R.color.colorPrimary)
                : h.itemView.getContext().getColor(R.color.colorBorder);
        h.puntoTimeline.setBackgroundTintList(ColorStateList.valueOf(color));
        h.barraRelativa.setProgressTintList(ColorStateList.valueOf(color));

        // Ocultar línea arriba en el primero, línea abajo en el último
        h.lineaArriba.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
        h.lineaAbajo.setVisibility(position == lista.size() - 1 ? View.INVISIBLE : View.VISIBLE);
    }

    private void parseFecha(String fecha, TextView tvDia, TextView tvMes) {
        if (fecha == null || fecha.length() < 10) {
            tvDia.setText("—");
            tvMes.setText("");
            return;
        }
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(fecha);
            if (d == null) return;
            tvDia.setText(new SimpleDateFormat("dd", Locale.getDefault()).format(d));
            tvMes.setText(new SimpleDateFormat("MMM", new Locale("es")).format(d));
        } catch (ParseException e) {
            tvDia.setText(fecha.substring(8, 10));
            tvMes.setText(fecha.substring(5, 7));
        }
    }

    @Override
    public int getItemCount() { return lista.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDia, tvMes, tvResultado, tvUnidad, tvPRBadge;
        View puntoTimeline, lineaArriba, lineaAbajo;
        ProgressBar barraRelativa;

        ViewHolder(View v) {
            super(v);
            tvDia         = v.findViewById(R.id.tvDia);
            tvMes         = v.findViewById(R.id.tvMes);
            tvResultado   = v.findViewById(R.id.tvResultado);
            tvUnidad      = v.findViewById(R.id.tvUnidad);
            tvPRBadge     = v.findViewById(R.id.tvPRBadge);
            puntoTimeline = v.findViewById(R.id.puntoTimeline);
            lineaArriba   = v.findViewById(R.id.lineaArriba);
            lineaAbajo    = v.findViewById(R.id.lineaAbajo);
            barraRelativa = v.findViewById(R.id.barraRelativa);
        }
    }
}
