package com.example.nautiluslibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LivrosAdapter extends ArrayAdapter<Livro> {

    private final Context context;
    private final ArrayList<Livro> elementos;

    public LivrosAdapter(Context context, ArrayList<Livro> elementos) {
        super(context, R.layout.linha, elementos);
        this.context = context;
        this.elementos = elementos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.linha, parent, false);
        TextView titulo = (TextView) rowView.findViewById(R.id.txtNome);
        TextView ano = (TextView) rowView.findViewById(R.id.txtAno);
        TextView autor = (TextView) rowView.findViewById(R.id.txtAutor);
        ImageView foto = (ImageView) rowView.findViewById(R.id.imageFoto);
        titulo.setText(elementos.get(position).getTitulo());
        autor.setText(elementos.get(position).getAutor());
        ano.setText(Integer.toString(elementos.get(position).getAno()));
        ano.setImage(Integer.toString(elementos.get(position).getFoto()));



        return rowView;
    }
}