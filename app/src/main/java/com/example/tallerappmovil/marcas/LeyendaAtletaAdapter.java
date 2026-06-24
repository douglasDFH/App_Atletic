package com.example.tallerappmovil.marcas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallerappmovil.R;

import java.util.ArrayList;
import java.util.List;

public class LeyendaAtletaAdapter extends RecyclerView.Adapter<LeyendaAtletaAdapter.VH> {

    public static class Item {
        final String nombre;
        final String mejorMarca;
        final int color;
        Item(String nombre, String mejorMarca, int color) {
            this.nombre = nombre; this.mejorMarca = mejorMarca; this.color = color;
        }
    }

    private List<Item> items = new ArrayList<>();

    public void setItems(List<Item> items, int[] colores) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leyenda_atleta, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Item item = items.get(position);
        holder.viewColor.setBackgroundColor(item.color);
        holder.tvNombre.setText(item.nombre);
        holder.tvMejorMarca.setText(item.mejorMarca);
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        View viewColor;
        TextView tvNombre, tvMejorMarca;
        VH(View v) {
            super(v);
            viewColor    = v.findViewById(R.id.viewColor);
            tvNombre     = v.findViewById(R.id.tvNombre);
            tvMejorMarca = v.findViewById(R.id.tvMejorMarca);
        }
    }
}
