package com.example.nautiluslibrary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EditarLivroActivity extends AppCompatActivity {

    private BDSQLiteHelper bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_livro);
        Intent intent = getIntent();
        final int id = intent.getIntExtra("ID", 0);
        bd = new BDSQLiteHelper(this);
        Livro livro = bd.getLivro(id);
        final EditText nome = (EditText) findViewById(R.id.etNome);
        final EditText autor = (EditText) findViewById(R.id.etAutor);
        final EditText ano = (EditText) findViewById(R.id.etAno);
        final EditText genero = (EditText) findViewById(R.id.etGenero);
        nome.setText(livro.getTitulo());
        autor.setText(livro.getAutor());
        ano.setText(String.valueOf(livro.getAno()));

        final Button alterar = (Button) findViewById(R.id.btnAlterar);
        alterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Livro livro = new Livro();
                livro.setId(id);
                livro.setTitulo(nome.getText().toString());
                livro.setAutor(autor.getText().toString());
                livro.setAno(Integer.parseInt(ano.getText().toString()));
                livro.setGenero(genero.getText().toString());
                bd.updateLivro(livro);
                Intent intent = new Intent(EditarLivroActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final Button remover = (Button) findViewById(R.id.btnRemover);
        remover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(EditarLivroActivity.this)
                        .setTitle(R.string.confirmar_exclusao)
                        .setMessage(R.string.quer_mesmo_apagar)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Livro livro = new Livro();
                                livro.setId(id);
                                bd.deleteLivro(livro);
                                Intent intent = new Intent(EditarLivroActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }
}