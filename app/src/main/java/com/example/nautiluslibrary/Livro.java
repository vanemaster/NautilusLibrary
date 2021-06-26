package com.example.nautiluslibrary;

import java.io.File;
import java.io.Serializable;

public class Livro implements Serializable {

    private int id;
    private String titulo;
    private String autor;
    private int ano;
    private byte[] foto;
    private String genero;

    public Livro() { }

    public Livro(int id, String titulo, String autor, int ano, byte[] foto, String genero) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.ano = ano;
        this.foto = foto;
        this.genero = genero;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public byte[] getFoto() { return foto; }

    public void setFoto(byte[] foto) { this.foto = foto; }

    public String getGenero() {return genero; }

    public void setGenero(String genero) { this.genero = genero; }
}
