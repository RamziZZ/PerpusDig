package com.example.perpusdig.model;

import java.util.List;

public class ResponseModelAnggota {
    private int kode;
    private String pesan;
    private List<DataModelAnggota> data;

    public int getKode() {
        return kode;
    }

    public void setKode(int kode) {
        this.kode = kode;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public List<DataModelAnggota> getData() {
        return data;
    }

    public void setData(List<DataModelAnggota> data) {
        this.data = data;
    }
}
