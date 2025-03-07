package com.example.perpusdig.model;

import java.util.List;

public class ResponseModelPinjamBuku {
    private int kode;
    private String pesan;
    private List<DataModelPinjamBuku> data;

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

    public List<DataModelPinjamBuku> getData() {
        return data;
    }

    public void setData(List<DataModelPinjamBuku> data) {
        this.data = data;
    }
}
