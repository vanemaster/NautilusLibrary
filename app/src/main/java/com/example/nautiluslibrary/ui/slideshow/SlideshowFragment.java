package com.example.nautiluslibrary.ui.slideshow;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nautiluslibrary.BDSQLiteHelper;
import com.example.nautiluslibrary.EditarLivroActivity;
import com.example.nautiluslibrary.Livro;
import com.example.nautiluslibrary.LivrosAdapter;
import com.example.nautiluslibrary.R;

import java.util.ArrayList;

public class SlideshowFragment extends Fragment {

    private BDSQLiteHelper bd;
    private LivrosAdapter adapter;
    ArrayList<Livro> listaLivros;

    public static final String CONTENT_AUTHORITY = "com.example.nautiluslibrary";
    public static final Uri BASE_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);
    public static final String PATH_CONTACTS = "nautiluslibrary";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_CONTACTS);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        bd = new BDSQLiteHelper(context);
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        String genero = "Romance";

        listaLivros = bd.getLivrosPorGenero(genero);
        ListView lista = (ListView) root.findViewById(R.id.lvLivros);
        LivrosAdapter adapter = new LivrosAdapter(getActivity(), listaLivros);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), EditarLivroActivity.class);
                intent.putExtra("ID", listaLivros.get(position).getId());
                startActivity(intent);
            }
        });

        return root;
    }
}