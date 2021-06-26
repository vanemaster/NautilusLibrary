package com.example.nautiluslibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        TextView autor = (TextView) rowView.findViewById(R.id.txtAutor);
        ImageView foto = (ImageView) rowView.findViewById(R.id.imageFoto);

        titulo.setText(elementos.get(position).getTitulo());
        autor.setText(elementos.get(position).getAutor());

        byte[] img = elementos.get(position).getFoto();
        Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);
        foto.setImageBitmap(Bitmap.createBitmap(bmp));

        return rowView;
    }
}