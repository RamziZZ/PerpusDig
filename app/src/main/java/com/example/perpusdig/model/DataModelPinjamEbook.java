package com.example.perpusdig.model;

public class DataModelPinjamEbook {
    private int id_user, id_buku, id_ebook;
    private String id_peminjaman, judul, kategori, status_peminjaman,
            tanggal_peminjaman, tanggal_pengembalian,nik_anggota,
            penulis, sampul;

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public int getId_buku() {
        return id_buku;
    }

    public void setId_buku(int id_buku) {
        this.id_buku = id_buku;
    }

    public int getId_ebook() {
        return id_ebook;
    }

    public void setId_ebook(int id_ebook) {
        this.id_ebook = id_ebook;
    }

    public String getId_peminjaman() {
        return id_peminjaman;
    }

    public void setId_peminjaman(String id_peminjaman) {
        this.id_peminjaman = id_peminjaman;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getStatus_peminjaman() {
        return status_peminjaman;
    }

    public void setStatus_peminjaman(String status_peminjaman) {
        this.status_peminjaman = status_peminjaman;
    }

    public String getTanggal_peminjaman() {
        return tanggal_peminjaman;
    }

    public void setTanggal_peminjaman(String tanggal_peminjaman) {
        this.tanggal_peminjaman = tanggal_peminjaman;
    }

    public String getTanggal_pengembalian() {
        return tanggal_pengembalian;
    }

    public void setTanggal_pengembalian(String tanggal_pengembalian) {
        this.tanggal_pengembalian = tanggal_pengembalian;
    }

    public String getNik_anggota() {
        return nik_anggota;
    }

    public void setNik_anggota(String nik_anggota) {
        this.nik_anggota = nik_anggota;
    }

    public String getPenulis() {
        return penulis;
    }

    public void setPenulis(String penulis) {
        this.penulis = penulis;
    }

    public String getSampul() {
        return sampul;
    }

    public void setSampul(String sampul) {
        this.sampul = sampul;
    }
}
