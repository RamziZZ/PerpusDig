package com.example.perpusdig.model;

import java.util.List;

public class ResponseModelPinjamEbook {
    private int kode;
    private String pesan;
    private List<DataModelPinjamEbook> data;

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

    public List<DataModelPinjamEbook> getData() {
        return data;
    }

    public void setData(List<DataModelPinjamEbook> data) {
        this.data = data;
    }
}
