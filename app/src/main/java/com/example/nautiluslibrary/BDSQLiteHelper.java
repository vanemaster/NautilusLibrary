package com.example.nautiluslibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.sql.Blob;
import java.util.ArrayList;

public class BDSQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "LivroDB";
    private static final String TABELA_LIVROS = "livros";
    private static final String ID = "id";
    private static final String TITULO = "titulo";
    private static final String AUTOR = "autor";
    private static final String ANO = "ano";
    private static final String FOTO = "foto";
    private static final String GENERO = "genero";
    private static final String[] COLUNAS = {ID, TITULO, AUTOR, ANO, FOTO, GENERO};

    public BDSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE livros ( "+
                "id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "titulo TEXT, "+
                "autor TEXT, "+
                "ano INTEGER, "+
                "foto BLOB, "+
                "genero TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS livros");
        this.onCreate(db);
    }

    public void addLivro(Livro livro) {
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "INSERT INTO livros (id,titulo,autor,ano,foto,genero) VALUES(?,?,?,?,?,?)";
        SQLiteStatement insertStmt = db.compileStatement(sql);
        insertStmt.clearBindings();
        insertStmt.bindNull(1);
        insertStmt.bindString(2,livro.getTitulo());
        insertStmt.bindString(3,livro.getAutor());
        insertStmt.bindLong(4,livro.getAno());
        insertStmt.bindBlob(5, livro.getFoto());
        insertStmt.bindString(6,livro.getGenero());

        insertStmt.executeInsert();

        db.close();
    }

    public Livro getLivro(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABELA_LIVROS, // a. tabela
                COLUNAS, // b. colunas
                " id = ?", // c. colunas para comparar
                new String[] { String.valueOf(id) }, // d. par??metros
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor == null) {
            return null;
        } else {
            cursor.moveToFirst();
            Livro livro = cursorToLivro(cursor);
            return livro;
        }
    }

    private Livro cursorToLivro(Cursor cursor) {
        Livro livro = new Livro();
        livro.setId(Integer.parseInt(cursor.getString(0)));
        livro.setTitulo(cursor.getString(1));
        livro.setAutor(cursor.getString(2));
        livro.setAno(Integer.parseInt(cursor.getString(3)));
        livro.setFoto(cursor.getBlob(4));
        livro.setGenero(cursor.getString(5));
        return livro;
    }

    public ArrayList<Livro> getAllLivros() {
        ArrayList<Livro> listaLivros = new ArrayList<Livro>();
        String query = "SELECT * FROM " + TABELA_LIVROS + " ORDER BY " + AUTOR;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Livro livro = cursorToLivro(cursor);
                listaLivros.add(livro);
            } while (cursor.moveToNext());
        }
        return listaLivros;
    }

    public ArrayList<Livro> getLivrosPorGenero(String genero) {
        ArrayList<Livro> listaLivros = new ArrayList<Livro>();
        String query = "SELECT * FROM " + TABELA_LIVROS + " WHERE "+ GENERO +" = '"+ genero +"' ORDER BY " + TITULO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Livro livro = cursorToLivro(cursor);
                listaLivros.add(livro);
            } while (cursor.moveToNext());
        }
        return listaLivros;
    }

    public void updateLivro(Livro livro) {
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "UPDATE livros SET id=?, titulo=?, autor=?, ano=?, foto=?, genero=? WHERE id=?";

        SQLiteStatement updateStmt = db.compileStatement(sql);
        updateStmt.clearBindings();
        updateStmt.bindLong(1,livro.getId());
        updateStmt.bindString(2,livro.getTitulo());
        updateStmt.bindString(3,livro.getAutor());
        updateStmt.bindLong(4,livro.getAno());
        updateStmt.bindBlob(5, livro.getFoto());
        updateStmt.bindString(6,livro.getGenero());
        updateStmt.bindLong(7, livro.getId());

        updateStmt.executeUpdateDelete();

        db.close();
    }

    public int deleteLivro(Livro livro) {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete(TABELA_LIVROS, //tabela
                ID+" = ?", // colunas para comparar
                new String[] { String.valueOf(livro.getId()) });
        db.close();
        return i; // n??mero de linhas exclu??das
    }
}
