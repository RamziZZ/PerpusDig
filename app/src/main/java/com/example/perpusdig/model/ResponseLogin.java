package com.example.perpusdig.model;

public class ResponseLogin {
    private String status;
    private String pesan;
    private Data data;

    public String getStatus() {
        return status;
    }

    public String getPesan() {
        return pesan;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        private int id_user;
        private String nik_anggota, username, email_user, nama_anggota, telp, alamat, foto_anggota, status_verifikasi, tgl_pendaftaran ;

        public int getId_user() {
            return id_user;
        }

        public String getNik_anggota() {
            return nik_anggota;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail_user() {
            return email_user;
        }

        public void setEmail_user(String email_user) {
            this.email_user = email_user;
        }

        public String getNama_anggota() {
            return nama_anggota;
        }

        public void setNama_anggota(String nama_anggota) {
            this.nama_anggota = nama_anggota;
        }

        public String getTelp() {
            return telp;
        }

        public void setTelp(String telp) {
            this.telp = telp;
        }

        public String getAlamat() {
            return alamat;
        }

        public void setAlamat(String alamat) {
            this.alamat = alamat;
        }

        public String getFoto_anggota() {
            return foto_anggota;
        }

        public void setFoto_anggota(String foto_anggota) {
            this.foto_anggota = foto_anggota;
        }

        public String getStatus_verifikasi() {
            return status_verifikasi;
        }

        public void setStatus_verifikasi(String status_verifikasi) {
            this.status_verifikasi = status_verifikasi;
        }

        public String getTgl_pendaftaran() {
            return tgl_pendaftaran;
        }

        public void setTgl_pendaftaran(String tgl_pendaftaran) {
            this.tgl_pendaftaran = tgl_pendaftaran;
        }
    }
}