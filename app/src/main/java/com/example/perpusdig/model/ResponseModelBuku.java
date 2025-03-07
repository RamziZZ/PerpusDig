package com.example.perpusdig.model;

import java.util.List;

public class ResponseModelBuku {
    private int kode;
    private String pesan;
    private List<DataModelBuku> data;

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

    public List<DataModelBuku> getData() {
        return data;
    }

    public void setData(List<DataModelBuku> data) {
        this.data = data;
    }
}