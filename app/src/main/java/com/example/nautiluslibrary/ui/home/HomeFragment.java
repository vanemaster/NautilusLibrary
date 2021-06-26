package com.example.nautiluslibrary.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.nautiluslibrary.BDSQLiteHelper;
import com.example.nautiluslibrary.EditarLivroActivity;
import com.example.nautiluslibrary.Livro;
import com.example.nautiluslibrary.LivrosAdapter;
import com.example.nautiluslibrary.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private BDSQLiteHelper bd;
    private LivrosAdapter adapter;
    ArrayList<Livro> listaLivros;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        bd = new BDSQLiteHelper(context);
//        adapter = new LivrosAdapter(context, listaLivros);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textView = root.findViewById(R.id.text_home);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        listaLivros = bd.getAllLivros();
        ListView lista = (ListView) root.findViewById(R.id.lvLivros);
        LivrosAdapter adapter = new LivrosAdapter(getActivity(), listaLivros);
        lista.setAdapter(adapter);
        Log.d("lista",listaLivros.toString());

//        for(String log : listaLivros)
//        {
//            Log.v("Tag",log);
//        }

        lista.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getActivity(), EditarLivroActivity.class);
            intent.putExtra("ID", listaLivros.get(position).getId());

            intent.putExtra("LivroCorrente", listaLivros.get(position) );

            startActivity(intent);
        });
        return root;
    }
}