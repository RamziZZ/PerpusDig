package com.example.perpusdig.model;

public class DataModelBuku {
    private int id_buku, tahun_terbit_buku, jumlah_buku;
    private String isbn, judul_buku, penulis_buku, penerbit_buku, deskripsi, kategori_buku, sampul_buku;

    public int getId_buku() {
        return id_buku;
    }

    public void setId_buku(int id_buku) {
        this.id_buku = id_buku;
    }

    public int getTahun_terbit_buku() {
        return tahun_terbit_buku;
    }

    public void setTahun_terbit_buku(int tahun_terbit_buku) {
        this.tahun_terbit_buku = tahun_terbit_buku;
    }

    public int getJumlah_buku() {
        return jumlah_buku;
    }

    public void setJumlah_buku(int jumlah_buku) {
        this.jumlah_buku = jumlah_buku;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getJudul_buku() {
        return judul_buku;
    }

    public void setJudul_buku(String judul_buku) {
        this.judul_buku = judul_buku;
    }

    public String getPenulis_buku() {
        return penulis_buku;
    }

    public void setPenulis_buku(String penulis_buku) {
        this.penulis_buku = penulis_buku;
    }

    public String getPenerbit_buku() {
        return penerbit_buku;
    }

    public void setPenerbit_buku(String penerbit_buku) {
        this.penerbit_buku = penerbit_buku;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getKategori_buku() {
        return kategori_buku;
    }

    public void setKategori_buku(String kategori_buku) {
        this.kategori_buku = kategori_buku;
    }

    public String getSampul_buku() {
        return sampul_buku;
    }

    public void setSampul_buku(String sampul_buku) {
        this.sampul_buku = sampul_buku;
    }
}