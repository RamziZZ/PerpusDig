package com.example.perpusdig.model;

import java.util.List;

public class ResponseModelEbook {
    private int kode;
    private String pesan;
    private List<DataModelEbook> data;

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

    public List<DataModelEbook> getData() {
        return data;
    }

    public void setData(List<DataModelEbook> data) {
        this.data = data;
    }
}
