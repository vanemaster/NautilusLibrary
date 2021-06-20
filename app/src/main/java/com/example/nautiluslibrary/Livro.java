package com.example.nautiluslibrary;

import java.io.Serializable;

public class Livro implements Serializable {

    private int id;
    private String titulo;
    private String autor;
    private int ano;
    private String foto;

    public Livro() { }

    public Livro(String titulo, String autor, int ano,String foto) {
        super();
        this.titulo = titulo;
        this.autor = autor;
        this.ano = ano;
        this.foto = foto;
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

    public String getFoto() { return foto; }

    public void setFoto(String foto) { this.foto = foto; }
}
